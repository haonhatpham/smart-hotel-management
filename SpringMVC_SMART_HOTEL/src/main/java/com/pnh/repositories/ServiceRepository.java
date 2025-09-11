/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories;

import com.pnh.pojo.Services;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface ServiceRepository {
    List<Services> getServices();
    Services getById(Long id);
    Services addOrUpdate(Services s);
    void delete(Long id);
}
