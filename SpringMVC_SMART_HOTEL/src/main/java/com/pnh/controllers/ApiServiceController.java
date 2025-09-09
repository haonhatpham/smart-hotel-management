///*
// * API Controller for Hotel Services - Client Side Only
// */
//package com.pnh.controllers;
//
//import com.pnh.pojo.Services;
//import com.pnh.pojo.ServiceOrders;
//import com.pnh.services.ServiceService;
//import java.util.List;
//import java.util.Map;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// *
// * @author ADMIN
// */
//@RestController
//@RequestMapping("/api")
//@CrossOrigin
//public class ApiServiceController {
//    
//    @Autowired
//    private ServiceService serviceService;
//    
//    @GetMapping("/services")
//    public ResponseEntity<List<Services>> list(@RequestParam Map<String, String> params) {
//        return new ResponseEntity<>(this.serviceService.getServices(params), HttpStatus.OK);
//    }
//    
//    @GetMapping("/services/{id}")
//    public ResponseEntity<Services> retrieve(@PathVariable(value = "id") Long id) {
//        Services service = this.serviceService.getServiceById(id);
//        if (service != null) {
//            return new ResponseEntity<>(service, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//    
//    @PostMapping("/services/order")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ServiceOrders orderService(@RequestBody ServiceOrders serviceOrder) {
//        return this.serviceService.addServiceOrder(serviceOrder);
//    }
//    
//    @GetMapping("/services/orders/{id}")
//    public ResponseEntity<ServiceOrders> getServiceOrder(@PathVariable(value = "id") Long id) {
//        ServiceOrders order = this.serviceService.getServiceOrderById(id);
//        if (order != null) {
//            return new ResponseEntity<>(order, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//}
