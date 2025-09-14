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
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.YearMonth;
import java.util.ArrayList;
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
    public List<Object[]> statsOccupancyByTime(String time, int year) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        // Tổng số phòng
        CriteriaQuery<Long> cqRooms = cb.createQuery(Long.class);
        Root<Rooms> roomRoot = cqRooms.from(Rooms.class);
        cqRooms.select(cb.count(roomRoot));
        Long totalRooms = session.createQuery(cqRooms).getSingleResult();

        // Room-nights booked
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<ReservationRooms> rr = cq.from(ReservationRooms.class);
        Join<ReservationRooms, Reservations> r = rr.join("reservationId", JoinType.INNER);

        // Lọc reservation theo trạng thái và năm
        Predicate statusPredicate = r.get("status").in("CONFIRMED", "CHECKED_IN", "CHECKED_OUT");
        Predicate yearPredicate = cb.equal(cb.function("YEAR", Integer.class, rr.get("checkOut")), year);
        cq.where(cb.and(statusPredicate, yearPredicate));

        // Số đêm và nhóm theo time
        Expression<Integer> nights = cb.function("DATEDIFF", Integer.class, rr.get("checkOut"), rr.get("checkIn"));
        Expression<Integer> periodExpr = cb.function(time, Integer.class, rr.get("checkOut"));

        cq.multiselect(periodExpr, cb.sum(nights));
        cq.groupBy(periodExpr);
        cq.orderBy(cb.asc(periodExpr));

        List<Object[]> results = session.createQuery(cq).getResultList();

        // Tính occupancy %
        List<Object[]> finalResult = new ArrayList<>();
        for (Object[] row : results) {
            int period = (Integer) row[0];
            long bookedNights = ((Number) row[1]).longValue();

            // Tính tổng số đêm có thể đặt được
            long totalPossibleNights;
            if ("MONTH".equalsIgnoreCase(time)) {
                YearMonth ym = YearMonth.of(year, period);
                totalPossibleNights = totalRooms * ym.lengthOfMonth();
            } else if ("QUARTER".equalsIgnoreCase(time)) {
                int daysInQuarter = 0;
                for (int m = (period - 1) * 3 + 1; m <= (period * 3); m++) {
                    daysInQuarter += YearMonth.of(year, m).lengthOfMonth();
                }
                totalPossibleNights = totalRooms * daysInQuarter;
            } else { // YEAR
                int daysInYear = YearMonth.of(year, 12).atEndOfMonth().getDayOfYear();
                totalPossibleNights = totalRooms * daysInYear;
            }

            double occupancyRate = totalPossibleNights == 0 ? 0
                    : (double) bookedNights / totalPossibleNights * 100.0;

            finalResult.add(new Object[]{period, bookedNights, totalPossibleNights, occupancyRate});
        }

        return finalResult;
    }

    @Override
    public List<Object[]> statsAvgRatingByTime(String time, int year) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Reviews> root = cq.from(Reviews.class);

        Expression<Integer> periodExpr = cb.function(time, Integer.class, root.get("createdAt"));

        cq.multiselect(periodExpr, cb.avg(root.get("rating")), cb.count(root));
        cq.where(cb.and(
                cb.equal(cb.function("YEAR", Integer.class, root.get("createdAt")), year),
                cb.isTrue(root.get("visible"))
        ));
        cq.groupBy(periodExpr);
        cq.orderBy(cb.asc(periodExpr));

        return s.createQuery(cq).getResultList();
    }

}
