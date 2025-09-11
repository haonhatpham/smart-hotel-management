/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories;

import com.pnh.pojo.Users;
import com.pnh.pojo.CustomerProfiles;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface UserRepository {

    public Users getUserByUsername(String username);

    Users addUser(Users u);
    boolean authenticate(String username, String password);

    CustomerProfiles getCustomerProfileByUsername(String username);
}
