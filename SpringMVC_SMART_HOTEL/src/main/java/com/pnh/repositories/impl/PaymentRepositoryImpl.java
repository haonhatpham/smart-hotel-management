/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories.impl;

import com.pnh.pojo.Payments;
import com.pnh.repositories.PaymentRepository;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author User
 */
@Repository
@Transactional
public class PaymentRepositoryImpl implements PaymentRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    @Override
    public Payments createPayment(Payments payment) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(payment);
        return payment;    
    }

    @Override
    public Payments updatePayment(Payments payment) {
        Session session = this.factory.getObject().getCurrentSession();
        session.merge(payment);
        return payment; 
    }

    @Override
    public Payments getPaymentById(Long id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.find(Payments.class, id);
        }

    @Override
    public List<Payments> getPaymentsByReservationId(Long reservationId) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Payments> query = session.createQuery(
            "FROM Payments p WHERE p.reservationId.id = :reservationId", 
            Payments.class
        );
        query.setParameter("reservationId", reservationId);
        return query.getResultList();  
    }


    @Override
    public List<Payments> getPaymentsByStatus(String status) {
       Session session = this.factory.getObject().getCurrentSession();
        Query<Payments> query = session.createQuery(
            "FROM Payments p WHERE p.status = :status", 
            Payments.class
        );
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public Payments getPaymentByTransactionId(String transactionId) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Payments> query = session.createQuery(
            "FROM Payments p WHERE p.transactionId = :transactionId", 
            Payments.class
        );
        query.setParameter("transactionId", transactionId);
        return query.uniqueResult();    }
    
}
