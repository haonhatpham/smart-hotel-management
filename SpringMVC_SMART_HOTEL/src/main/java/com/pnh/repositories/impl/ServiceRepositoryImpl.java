/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories.impl;

import com.pnh.pojo.Services;
import com.pnh.repositories.ServiceRepository;
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
public class ServiceRepositoryImpl implements ServiceRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Services> getServices() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Services", Services.class);
        return q.getResultList();
    }

    @Override
    public Services getById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(Services.class, id);
    }

    @Override
    public Services addOrUpdate(Services svc) {
        Session s = this.factory.getObject().getCurrentSession();
        if (svc.getId() == null) {
            s.persist(svc);
            return svc;
        }
        return (Services) s.merge(svc);
    }

    @Override
    public void delete(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Services sv = this.getById(id);
        if (sv != null) s.remove(sv);
    }
}
