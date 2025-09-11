/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pnh.services;

import com.pnh.pojo.Services;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface ServiceService {
    List<Services> getServices();
    Services getById(Long id);
    Services addOrUpdate(Services s);
    void delete(Long id);
}
