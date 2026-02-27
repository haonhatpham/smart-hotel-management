package com.pnh.repositories.impl;

import com.pnh.pojo.LoyaltyLog;
import com.pnh.repositories.LoyaltyLogRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class LoyaltyLogRepositoryImpl implements LoyaltyLogRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public LoyaltyLog save(LoyaltyLog log) {
        Session s = factory.getObject().getCurrentSession();
        s.persist(log);
        return log;
    }

    @Override
    public List<LoyaltyLog> findByUserIdOrderByCreatedAtDesc(Long userId, int limit) {
        Session s = factory.getObject().getCurrentSession();
        Query<LoyaltyLog> q = s.createQuery("FROM LoyaltyLog l WHERE l.userId = :uid ORDER BY l.createdAt DESC", LoyaltyLog.class);
        q.setParameter("uid", userId);
        if (limit > 0) q.setMaxResults(limit);
        return q.getResultList();
    }
}
