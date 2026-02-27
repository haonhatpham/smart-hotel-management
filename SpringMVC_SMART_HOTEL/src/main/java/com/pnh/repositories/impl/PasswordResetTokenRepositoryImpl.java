package com.pnh.repositories.impl;

import com.pnh.pojo.PasswordResetToken;
import com.pnh.repositories.PasswordResetTokenRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        Session s = factory.getObject().getCurrentSession();
        if (token.getId() == null) {
            s.persist(token);
            return token;
        }
        return (PasswordResetToken) s.merge(token);
    }

    @Override
    public PasswordResetToken findByToken(String token) {
        Session s = factory.getObject().getCurrentSession();
        List<PasswordResetToken> list = s.createQuery(
                "FROM com.pnh.pojo.PasswordResetToken t WHERE t.token = :token", PasswordResetToken.class)
                .setParameter("token", token)
                .setMaxResults(1)
                .list();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void deleteByToken(String token) {
        Session s = factory.getObject().getCurrentSession();
        s.createQuery("DELETE FROM com.pnh.pojo.PasswordResetToken t WHERE t.token = :token")
                .setParameter("token", token)
                .executeUpdate();
    }

    @Override
    public void deleteExpired() {
        Session s = factory.getObject().getCurrentSession();
        s.createQuery("DELETE FROM com.pnh.pojo.PasswordResetToken t WHERE t.expiresAt < CURRENT_TIMESTAMP")
                .executeUpdate();
    }
}
