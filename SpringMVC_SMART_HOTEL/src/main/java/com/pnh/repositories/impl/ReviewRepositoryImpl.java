package com.pnh.repositories.impl;

import com.pnh.pojo.Reviews;
import com.pnh.repositories.ReviewRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class ReviewRepositoryImpl implements ReviewRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Reviews findByReservationId(Long reservationId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query<Reviews> q = s.createQuery("FROM Reviews r WHERE r.reservationId.id = :rid", Reviews.class);
        q.setParameter("rid", reservationId);
        return q.uniqueResultOptional().orElse(null);
    }

    @Override
    public Reviews addOrUpdate(Reviews review) {
        Session s = this.factory.getObject().getCurrentSession();
        s.merge(review);
        return review;
    }
}


