/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories.impl;

import com.pnh.pojo.Users;
import com.pnh.repositories.UserRepository;
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
    public Users addUser(Users u) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(u);

        return u;
    }

    @Override
    public boolean authenticate(String username, String password) {
        System.out.println("[AUTH DEBUG] repo.authenticate username=" + username);
        Users u = this.getUserByUsername(username);
        if (u == null) {
            System.out.println("[AUTH DEBUG] repo: user not found");
            return false;
        }
        String storedHash = u.getPassword();
        System.out.println("[AUTH DEBUG] repo: stored hash=" + storedHash);
        System.out.println("[AUTH DEBUG] repo: input password length=" + (password == null ? 0 : password.length()));
        boolean ok = this.passwordEncoder.matches(password, storedHash);
        System.out.println("[AUTH DEBUG] repo: BCrypt.matches => " + ok);
        return ok;
    }
}
