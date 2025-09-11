/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories.impl;

import com.pnh.pojo.Invoices;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.Reservations;
import com.pnh.repositories.ReservationRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ReservationRepositoryImpl implements ReservationRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    private static final int DEFAULT_PAGE_SIZE = 6;

    @Override
    public Reservations getById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(Reservations.class, id);
    }

    @Override
    public Reservations addOrUpdate(Reservations reservation) {
        Session s = this.factory.getObject().getCurrentSession();
        if (reservation.getId() == null) {
            s.persist(reservation);
            return reservation;
        }
        return (Reservations) s.merge(reservation);
    }

    @Override
    public Reservations updateStatus(Long id, String status) {
        Session s = this.factory.getObject().getCurrentSession();
        Reservations r = s.find(Reservations.class, id);
        if (r == null) return null;
        r.setStatus(status);
        return (Reservations) s.merge(r);
    }

    @Override
    public List<ReservationRooms> getReservationRooms(Long reservationId) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.createQuery("FROM ReservationRooms rr WHERE rr.reservationId.id = :rid ORDER BY rr.id", ReservationRooms.class)
                .setParameter("rid", reservationId)
                .getResultList();
    }

    @Override
    public void replaceReservationRooms(Long reservationId, List<ReservationRooms> items) {
        Session s = this.factory.getObject().getCurrentSession();
        s.createQuery("DELETE FROM ReservationRooms rr WHERE rr.reservationId.id = :rid")
                .setParameter("rid", reservationId)
                .executeUpdate();
        if (items != null) {
            for (ReservationRooms rr : items) {
                s.persist(rr);
            }
        }
    }

    @Override
    public Invoices getInvoiceByReservationId(Long reservationId) {
        Session s = this.factory.getObject().getCurrentSession();
        List<Invoices> list = s.createQuery("FROM com.pnh.pojo.Invoices i WHERE i.reservationId.id = :rid", Invoices.class)
                .setParameter("rid", reservationId)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Invoices addOrUpdateInvoice(Invoices invoice) {
        Session s = this.factory.getObject().getCurrentSession();
        if (invoice.getId() == null) {
            s.persist(invoice);
            return invoice;
        }
        return (Invoices) s.merge(invoice);
    }

    @Override
    public boolean hasOverlapForAnyRoom(List<Long> roomIds, LocalDate checkIn, LocalDate checkOut, List<String> statuses) {
        if (roomIds == null || roomIds.isEmpty()) return false;
        Session s = this.factory.getObject().getCurrentSession();

        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> query = b.createQuery(Long.class);
        Root<ReservationRooms> rr = query.from(ReservationRooms.class);
        Join<ReservationRooms, Reservations> res = rr.join("reservationId");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(rr.get("roomId").get("id").in(roomIds));
        if (statuses != null && !statuses.isEmpty()) {
            predicates.add(res.get("status").in(statuses));
        }
        predicates.add(b.not(b.or(
                b.lessThanOrEqualTo(res.get("checkOut"), checkOut),
                b.greaterThanOrEqualTo(res.get("checkIn"), checkIn)
        )));

        query.select(b.count(rr)).where(b.and(predicates.toArray(new Predicate[0])));
        Long count = s.createQuery(query).getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<Reservations> getReservations(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        var b = s.getCriteriaBuilder();
        var q = b.createQuery(Reservations.class);
        var root = q.from(Reservations.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String customerId = params.get("customerId");
            if (customerId != null && !customerId.isEmpty()) {
                predicates.add(b.equal(root.get("customerId").get("id"), Long.valueOf(customerId)));
            }
            String status = params.get("status");
            if (status != null && !status.isEmpty()) {
                predicates.add(b.equal(root.get("status"), status));
            }
            if (!predicates.isEmpty()) {
                q.where(b.and(predicates.toArray(new Predicate[0])));
            }
            q.orderBy(b.desc(root.get("createdAt")));
        }

        var query = s.createQuery(q);

        if (params != null) {
            int size = DEFAULT_PAGE_SIZE;
            int page = 1;
            try {
                String sizeStr = params.get("size");
                if (sizeStr != null && !sizeStr.isEmpty()) size = Integer.parseInt(sizeStr);
                String pageStr = params.get("page");
                if (pageStr != null && !pageStr.isEmpty()) page = Integer.parseInt(pageStr);
            } catch (NumberFormatException ignore) {}
            if (size <= 0) size = DEFAULT_PAGE_SIZE;
            if (page <= 0) page = 1;
            int start = (page - 1) * size;
            query.setFirstResult(start).setMaxResults(size);
        }

        return query.getResultList();
    }
}
