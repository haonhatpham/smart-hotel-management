/*
 * API Controller for Services - Read Only
 */
package com.pnh.controllers;

import com.pnh.services.ServiceService;
import com.pnh.pojo.Services;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiServiceController {
    @Autowired
    private ServiceService serviceService;
    
    @GetMapping("/services")
    public ResponseEntity<List<Services>> list() {
        return new ResponseEntity<>(this.serviceService.getServices(), HttpStatus.OK);
    }
}