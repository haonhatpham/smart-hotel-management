package com.pnh.repositories.impl;

import com.pnh.pojo.Invoices;
import com.pnh.repositories.InvoiceRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class InvoiceRepositoryImpl implements InvoiceRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Invoices save(Invoices invoice) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(invoice); 
        return invoice;
    }

    @Override
    public Optional<Invoices> findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Invoices> cq = cb.createQuery(Invoices.class);
        Root<Invoices> root = cq.from(Invoices.class);
        cq.select(root).where(cb.equal(root.get("id"), id));

        Invoices invoice = session.createQuery(cq).uniqueResult();
        return Optional.ofNullable(invoice);
    }

    @Override
    public boolean existsByReservationId(Long reservationId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Invoices> root = cq.from(Invoices.class);
        cq.select(cb.count(root))
          .where(cb.equal(root.get("reservation").get("id"), reservationId));

        Long count = session.createQuery(cq).uniqueResult();
        return count != null && count > 0;
    }
    
    @Override
    public Optional<Invoices> findByIdWithDetails(Long id) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Invoices> cq = cb.createQuery(Invoices.class);
        Root<Invoices> invoiceRoot = cq.from(Invoices.class);
        Join<Object, Object> reservationJoin = invoiceRoot.join("reservation", JoinType.INNER);
        reservationJoin.fetch("customer", JoinType.INNER);
        reservationJoin.fetch("reservationRooms", JoinType.LEFT);
        reservationJoin.fetch("reservationServices", JoinType.LEFT);

        cq.select(invoiceRoot).where(cb.equal(invoiceRoot.get("id"), id));

        Invoices invoice = session.createQuery(cq).uniqueResult();
        return Optional.ofNullable(invoice);
    }


}