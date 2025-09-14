package com.pnh.controllers;

import com.pnh.dto.ReservationCreateDTO;
import com.pnh.pojo.Reservations;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.ServiceOrders;
import com.pnh.pojo.Invoices;
import com.pnh.services.ReservationService;
import com.pnh.services.UserService;
import com.pnh.services.RoomService;
import com.pnh.services.ServiceService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API Controller for Hotel Reservations - Client Side Only
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ServiceService serviceService;

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservations>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.reservationService.getReservations(params), HttpStatus.OK);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<?> retrieve(@PathVariable(value = "id") Long id) {
        Reservations r = this.reservationService.getById(id);
        if (r == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ReservationRooms> rooms = this.reservationService.getReservationRooms(id);
        List<ServiceOrders> services = this.reservationService.getServiceOrders(id);
        var invoice = this.reservationService.getInvoiceByReservationId(id);

        var resp = new java.util.HashMap<String, Object>();
        resp.put("reservation", r);
        resp.put("rooms", rooms);
        resp.put("services", services);
        resp.put("invoice", invoice);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody ReservationCreateDTO dto) {
        try {
            Reservations savedReservation = this.reservationService.createFromDTO(dto);

            // Trả về response với ID rõ ràng
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedReservation.getId());
            response.put("reservationId", savedReservation.getId());
            response.put("status", savedReservation.getStatus());
            response.put("message", "Reservation created successfully");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("[ERROR] Exception in create: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "Failed to create reservation");

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<Reservations> update(@PathVariable("id") Long id, @RequestBody Reservations reservation) {
        reservation.setId(id);
        Reservations updated = this.reservationService.addOrUpdate(reservation);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PostMapping("/reservations/{id}/confirm")
    public ResponseEntity<Reservations> confirm(@PathVariable(value = "id") Long id) {
        Reservations confirmed = this.reservationService.updateStatus(id, "CONFIRMED");
        if (confirmed != null) {
            return new ResponseEntity<>(confirmed, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/reservations/{id}/cancel")
    public ResponseEntity<Reservations> cancelReservation(@PathVariable(value = "id") Long id) {
        Reservations cancelledReservation = this.reservationService.updateStatus(id, "CANCELLED");
        if (cancelledReservation != null) {
            return new ResponseEntity<>(cancelledReservation, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
