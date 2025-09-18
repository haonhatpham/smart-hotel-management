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



}