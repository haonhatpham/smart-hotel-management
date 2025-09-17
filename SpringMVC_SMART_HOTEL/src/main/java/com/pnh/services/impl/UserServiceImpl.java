/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.pojo.Users;
import com.pnh.pojo.CustomerProfiles;
import com.pnh.repositories.UserRepository;
import com.pnh.services.UserService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ADMIN
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Users getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public Users getById(Long id) {
        return this.userRepo.getById(id);
    }

    //phương thức của UserDetailsService tự động gọi khi xác thực
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users u = this.getUserByUsername(username);
        if (u == null) {
            System.out.println("[AUTH DEBUG] user not found");
            throw new UsernameNotFoundException("Invalid username");
        }
        System.out.println("[AUTH DEBUG] user found: id=" + u.getId() + ", role=" + u.getRole() + ", enabled=" + u.getEnabled());
        System.out.println("[AUTH DEBUG] stored hash =" + (u.getPassword()));
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + u.getRole()));

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }

    @Override
    public Users addUser(Map<String, String> params, MultipartFile avatar) {
        Users u = new Users();
        u.setFullName(params.get("fullname"));
        u.setEmail(params.get("email"));
        u.setPhone(params.get("phone"));
        u.setUsername(params.get("username"));
        u.setPassword(this.passwordEncoder.encode(params.get("password")));
        u.setRole("CUSTOMER");
        u.setEnabled(true);
        u.setCreatedAt(new Date());
        
        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return this.userRepo.addUser(u);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.userRepo.authenticate(username, password);
    }

    @Override
    public CustomerProfiles getCustomerProfile(String username) {
        return this.userRepo.getCustomerProfileByUsername(username);
    }

    @Override
    public Users getUserByEmail(String email) {
        return this.userRepo.getUserByEmail(email);
    }

    @Override
    public Users createUserFromGoogle(String email, String fullname, String avatarUrl) {
        Users existing = null;
        try {
            existing = this.userRepo.getUserByEmail(email);
        } catch (Exception ex) {
            existing = null;
        }
        if (existing != null) {
            return existing;
        }

        Users u = new Users();
        u.setEmail(email);
        u.setUsername(fullname != null ? fullname : email);
        u.setUsername(email);
        u.setRole("CUSTOMER");
        u.setPassword("1");
        u.setEnabled(true);
        u.setCreatedAt(new Date());
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            u.setAvatar(avatarUrl);
        }
        return this.userRepo.addUser(u);
    }
}
