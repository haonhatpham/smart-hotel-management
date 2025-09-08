/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services;

import com.pnh.pojo.Users;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ADMIN
 */
public interface UserService extends UserDetailsService {

    Users getUserByUsername(String username);

    Users addUser(Map<String, String> params, MultipartFile avatar);

    boolean authenticate(String username, String password);

}
