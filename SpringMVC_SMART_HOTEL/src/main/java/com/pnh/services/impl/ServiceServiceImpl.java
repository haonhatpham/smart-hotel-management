/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.pojo.Services;
import com.pnh.repositories.ServiceRepository;
import com.pnh.services.ServiceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ADMIN
 */
@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepo;

    @Override
    public List<Services> getServices() {
        return this.serviceRepo.getServices();
    }

    @Override
    public Services getById(Long id) {
        return this.serviceRepo.getById(id);
    }

    @Override
    public Services addOrUpdate(Services s) {
        return this.serviceRepo.addOrUpdate(s);
    }

    @Override
    public void delete(Long id) {
        this.serviceRepo.delete(id);
    }
}
