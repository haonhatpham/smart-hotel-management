/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories.impl;

import com.pnh.pojo.Invoices;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.Reservations;
import com.pnh.pojo.ServiceOrders;
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
        if (r == null) {
            return null;
        }
        r.setStatus(status);
        return (Reservations) s.merge(r);
    }

    @Override
    public List<ReservationRooms> getReservationRooms(Long reservationId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<ReservationRooms> cq = cb.createQuery(ReservationRooms.class);
        Root<ReservationRooms> root = cq.from(ReservationRooms.class);

        cq.select(root)
                .where(cb.equal(root.get("reservationId").get("id"), reservationId))
                .orderBy(cb.asc(root.get("id")));

        return s.createQuery(cq).getResultList();
    }

    @Override
    public List<ServiceOrders> getServiceOrders(Long reservationId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<ServiceOrders> cq = cb.createQuery(ServiceOrders.class);
        Root<ServiceOrders> root = cq.from(ServiceOrders.class);

        cq.select(root)
                .where(cb.equal(root.get("reservationId").get("id"), reservationId))
                .orderBy(cb.asc(root.get("orderedAt")));

        return s.createQuery(cq).getResultList();
    }

    @Override
    public Invoices getInvoiceByReservationId(Long reservationId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Invoices> cq = cb.createQuery(Invoices.class);
        Root<Invoices> root = cq.from(Invoices.class);

        cq.select(root)
                .where(cb.equal(root.get("reservationId").get("id"), reservationId));

        List<Invoices> result = s.createQuery(cq)
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
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
            if (customerId != null) {
                customerId = customerId.trim();
                if (!customerId.isEmpty()) {
                    try {
                        predicates.add(b.equal(root.get("customerId").get("id"), Long.valueOf(customerId)));
                    } catch (NumberFormatException ex) {
                        // Bỏ qua filter nếu id không hợp lệ để tránh 500
                    }
                }
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

        // Phân trang
        if (params != null) {
            String p = params.get("page");
            if (p != null && !p.isEmpty()) {
                int page = Integer.parseInt(p);
                int start = (page - 1) * DEFAULT_PAGE_SIZE;

                query.setMaxResults(DEFAULT_PAGE_SIZE);
                query.setFirstResult(start);
            }
        }

        return query.getResultList();
    }

    @Override
    public ReservationRooms addOrUpdateReservationRoom(ReservationRooms reservationRoom) {
        Session s = this.factory.getObject().getCurrentSession();
        if (reservationRoom.getId() == null) {
            s.persist(reservationRoom);
            return reservationRoom;
        }
        return (ReservationRooms) s.merge(reservationRoom);
    }

    @Override
    public ServiceOrders addOrUpdateServiceOrder(ServiceOrders serviceOrder) {
        Session s = this.factory.getObject().getCurrentSession();
        if (serviceOrder.getId() == null) {
            s.persist(serviceOrder);
            return serviceOrder;
        }
        return (ServiceOrders) s.merge(serviceOrder);
    }

}
