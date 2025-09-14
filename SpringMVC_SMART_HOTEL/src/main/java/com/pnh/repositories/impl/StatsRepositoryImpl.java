/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories.impl;

import com.pnh.repositories.StatsRepository;
import com.pnh.pojo.Invoices;
import com.pnh.pojo.Reservations;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.Reviews;
import com.pnh.pojo.Rooms;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
public class StatsRepositoryImpl implements StatsRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Object[]> statsRevenueByTime(String time, int year) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = b.createQuery(Object[].class);
        Root<Invoices> root = q.from(Invoices.class);

        q.multiselect(
            b.function(time, Integer.class, root.get("issuedAt")),
            b.sum(root.get("totalAmount"))
        );

        q.where(b.equal(b.function("YEAR", Integer.class, root.get("issuedAt")), year));
        q.groupBy(b.function(time, Integer.class, root.get("issuedAt")));

        Query<Object[]> query = s.createQuery(q);
        List<Object[]> result = query.getResultList();
                
        return result;
    }

    @Override
    public List<Object[]> statsOccupancyByMonth(int year) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = b.createQuery(Object[].class);
        Root<Reservations> root = q.from(Reservations.class);
        // Join từng phòng trong đơn để đếm room-nights đúng = số đêm × số phòng
        Join<Reservations, ReservationRooms> joinRooms = root.join("reservationRoomsSet", JoinType.INNER);

        q.multiselect(
            b.function("MONTH", Integer.class, root.get("checkOut")),
            b.sum(
                b.function("DATEDIFF", Integer.class, root.get("checkOut"), root.get("checkIn"))
            )
        );

        q.where(
            b.equal(b.function("YEAR", Integer.class, root.get("checkOut")), year),
            root.get("status").in("CONFIRMED", "CHECKED_IN", "CHECKED_OUT")
        );
        q.groupBy(b.function("MONTH", Integer.class, root.get("checkOut")));

        Query<Object[]> query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public List<Object[]> statsAvgRatingByMonth(int year) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = b.createQuery(Object[].class);
        Root<Reviews> root = q.from(Reviews.class);

        q.multiselect(
            b.function("MONTH", Integer.class, root.get("createdAt")),
            b.avg(root.get("rating")),
            b.count(root)
        );

        q.where(
            b.equal(b.function("YEAR", Integer.class, root.get("createdAt")), year),
            b.isTrue(root.get("visible"))
        );
        q.groupBy(b.function("MONTH", Integer.class, root.get("createdAt")));

        Query<Object[]> query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public long countAllRooms() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Rooms> root = q.from(Rooms.class);
        q.select(b.count(root))
         .where(root.get("status").in("AVAILABLE", "OCCUPIED", "CLEANING"));
        return s.createQuery(q).getSingleResult();
    }
}
