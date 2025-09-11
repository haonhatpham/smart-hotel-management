/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories.impl;

import com.pnh.pojo.RoomTypes;
import com.pnh.repositories.RoomTypeRepository;
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
public class RoomTypeRepositoryImpl implements RoomTypeRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<RoomTypes> getRoomTypes() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("From RoomTypes", RoomTypes.class);
        return q.getResultList();
    }

    @Override
    public RoomTypes getById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(RoomTypes.class, id);
    }

    @Override
    public RoomTypes addOrUpdate(RoomTypes rt) {
        Session s = this.factory.getObject().getCurrentSession();
        if (rt.getId() == null) {
            s.persist(rt);
            return rt;
        }
        return (RoomTypes) s.merge(rt);
    }

    @Override
    public void delete(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        RoomTypes rt = this.getById(id);
        if (rt != null) s.remove(rt);
    }
}
