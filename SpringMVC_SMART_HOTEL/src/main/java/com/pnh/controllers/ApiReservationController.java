///*
// * API Controller for Hotel Reservations - Client Side Only
// */
//package com.pnh.controllers;
//
//import com.pnh.pojo.Reservations;
//import com.pnh.pojo.ReservationRooms;
//import com.pnh.pojo.Invoices;
//import com.pnh.services.ReservationService;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
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
//public class ApiReservationController {
//    
//    @Autowired
//    private ReservationService reservationService;
//    
//    @GetMapping("/reservations/{id}")
//    public ResponseEntity<Reservations> retrieve(@PathVariable(value = "id") Long id) {
//        Reservations reservation = this.reservationService.getReservationById(id);
//        if (reservation != null) {
//            return new ResponseEntity<>(reservation, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//    
//    @PostMapping("/reservations")
//    @ResponseStatus(HttpStatus.CREATED)
//    public Reservations create(@RequestBody Reservations reservation) {
//        return this.reservationService.addOrUpdateReservation(reservation);
//    }
//    
//    @PostMapping("/reservations/{id}/cancel")
//    public ResponseEntity<Reservations> cancelReservation(@PathVariable(value = "id") Long id) {
//        Reservations cancelledReservation = this.reservationService.cancelReservation(id);
//        if (cancelledReservation != null) {
//            return new ResponseEntity<>(cancelledReservation, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//    
//    @GetMapping("/reservations/{id}/rooms")
//    public ResponseEntity<List<ReservationRooms>> getReservationRooms(@PathVariable(value = "id") Long id) {
//        List<ReservationRooms> reservationRooms = this.reservationService.getReservationRooms(id);
//        return new ResponseEntity<>(reservationRooms, HttpStatus.OK);
//    }
//    
//    @GetMapping("/reservations/{id}/invoice")
//    public ResponseEntity<Invoices> getInvoice(@PathVariable(value = "id") Long id) {
//        Invoices invoice = this.reservationService.getInvoiceByReservationId(id);
//        if (invoice != null) {
//            return new ResponseEntity<>(invoice, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//    
//    @PostMapping("/reservations/{id}/checkout")
//    public ResponseEntity<Invoices> checkout(@PathVariable(value = "id") Long id) {
//        Invoices invoice = this.reservationService.processCheckout(id);
//        if (invoice != null) {
//            return new ResponseEntity<>(invoice, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }
//}
