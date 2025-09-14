/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories.impl;

import com.pnh.pojo.Users;
import com.pnh.pojo.CustomerProfiles;
import com.pnh.repositories.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Users getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createNamedQuery("Users.findByUsername", Users.class);
        q.setParameter("username", username);

        return (Users) q.getSingleResult();
    }

    @Override
    public Users getById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.find(Users.class, id);
    }

    @Override
    public Users addUser(Users u) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(u);

        return u;
    }

    @Override
    public boolean authenticate(String username, String password) {
        Users u = this.getUserByUsername(username);
        if (u == null) {
            return false;
        }
        String storedHash = u.getPassword();
        boolean ok = this.passwordEncoder.matches(password, storedHash);
        return ok;
    }

    @Override
    public CustomerProfiles getCustomerProfileByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<CustomerProfiles> cq = cb.createQuery(CustomerProfiles.class);

        Root<Users> root = cq.from(Users.class);

        cq.select(root.get("customerProfile"))
                .where(cb.equal(root.get("username"), username));

        return s.createQuery(cq).uniqueResult();
    }

}
