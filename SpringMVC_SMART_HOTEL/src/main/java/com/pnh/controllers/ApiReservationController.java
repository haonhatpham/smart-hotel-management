package com.pnh.controllers;

import com.pnh.dto.ReservationCreateDTO;
import com.pnh.pojo.Reservations;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.ServiceOrders;
import com.pnh.pojo.Invoices;
import com.pnh.pojo.Users;
import com.pnh.services.ReservationService;
import com.pnh.services.UserService;
import com.pnh.services.RoomService;
import com.pnh.services.ServiceService;
import java.security.Principal;
import java.util.HashMap;
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
import jakarta.validation.Valid;

/**
 * API Controller for Hotel Reservations - All endpoints under /api/secure/ require JWT.
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

    /** Chỉ cho phép nếu là chủ đơn hoặc ADMIN/RECEPTION. */
    private boolean canAccessReservation(Reservations r, Users currentUser) {
        if (currentUser == null || r == null || r.getCustomerId() == null) return false;
        if (r.getCustomerId().getId().equals(currentUser.getId())) return true;
        String role = currentUser.getRole() != null ? currentUser.getRole() : "";
        return "ADMIN".equals(role) || "RECEPTION".equals(role);
    }

    @GetMapping("/secure/reservations")
    public ResponseEntity<?> list(Principal principal, @RequestParam Map<String, String> params) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Chưa đăng nhập"));
        }
        Users currentUser = this.userService.getUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Không tìm thấy người dùng"));
        }
        Map<String, String> safeParams = params != null ? new HashMap<>(params) : new HashMap<>();
        if (!"ADMIN".equals(currentUser.getRole()) && !"RECEPTION".equals(currentUser.getRole())) {
            safeParams.put("customerId", String.valueOf(currentUser.getId()));
        }
        if (safeParams.containsKey("page")) {
            List<Reservations> items = this.reservationService.getReservations(safeParams);
            long total = this.reservationService.countReservations(safeParams);
            Map<String, Object> body = new HashMap<>();
            body.put("items", items);
            body.put("total", total);
            return new ResponseEntity<>(body, HttpStatus.OK);
        }
        return new ResponseEntity<>(this.reservationService.getReservations(safeParams), HttpStatus.OK);
    }

    @GetMapping("/secure/reservations/{id}")
    public ResponseEntity<?> retrieve(Principal principal, @PathVariable(value = "id") Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Chưa đăng nhập"));
        }
        Users currentUser = this.userService.getUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Không tìm thấy người dùng"));
        }
        Reservations r = this.reservationService.getById(id);
        if (r == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!canAccessReservation(r, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Bạn không có quyền xem đơn này"));
        }

        List<ReservationRooms> rooms = this.reservationService.getReservationRooms(id);
        List<ServiceOrders> services = this.reservationService.getServiceOrders(id);
        var invoice = this.reservationService.getInvoiceByReservationId(id);

        var resp = new HashMap<String, Object>();
        resp.put("reservation", r);
        resp.put("rooms", rooms);
        resp.put("services", services);
        resp.put("invoice", invoice);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping("/secure/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(Principal principal, @Valid @RequestBody ReservationCreateDTO dto) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Chưa đăng nhập"));
        }
        Users currentUser = this.userService.getUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Không tìm thấy người dùng"));
        }
        dto.setCustomerId(currentUser.getId());
        Reservations savedReservation = this.reservationService.createFromDTO(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("id", savedReservation.getId());
        response.put("reservationId", savedReservation.getId());
        response.put("status", savedReservation.getStatus());
        response.put("message", "Reservation created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/secure/reservations/{id}")
    public ResponseEntity<?> update(Principal principal, @PathVariable("id") Long id, @RequestBody Reservations reservation) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Chưa đăng nhập"));
        }
        Users currentUser = this.userService.getUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Không tìm thấy người dùng"));
        }
        Reservations existing = this.reservationService.getById(id);
        if (existing == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (!canAccessReservation(existing, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Bạn không có quyền sửa đơn này"));
        }
        reservation.setId(id);
        Reservations updated = this.reservationService.addOrUpdate(reservation);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PostMapping("/secure/reservations/{id}/confirm")
    public ResponseEntity<?> confirm(Principal principal, @PathVariable(value = "id") Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Chưa đăng nhập"));
        }
        Users currentUser = this.userService.getUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Không tìm thấy người dùng"));
        }
        Reservations existing = this.reservationService.getById(id);
        if (existing == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (!canAccessReservation(existing, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Bạn không có quyền xác nhận đơn này"));
        }
        Reservations confirmed = this.reservationService.updateStatus(id, "CONFIRMED");
        return new ResponseEntity<>(confirmed, HttpStatus.OK);
    }

    @PostMapping("/secure/reservations/{id}/cancel")
    public ResponseEntity<?> cancelReservation(Principal principal, @PathVariable(value = "id") Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Chưa đăng nhập"));
        }
        Users currentUser = this.userService.getUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Không tìm thấy người dùng"));
        }
        Reservations existing = this.reservationService.getById(id);
        if (existing == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (!canAccessReservation(existing, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Bạn không có quyền hủy đơn này"));
        }
        Reservations cancelledReservation = this.reservationService.updateStatus(id, "CANCELLED");
        return new ResponseEntity<>(cancelledReservation, HttpStatus.OK);
    }

    @GetMapping("/secure/recommendations/me")
    public ResponseEntity<?> getMyRecommendations(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Chưa đăng nhập"
            ));
        }

        var user = this.userService.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy người dùng"
            ));
        }

        Map<String, List<Long>> rec = this.reservationService.getRecommendationsForCustomer(user.getId());
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("roomTypeIds", rec.getOrDefault("roomTypeIds", List.of()));
        body.put("serviceIds", rec.getOrDefault("serviceIds", List.of()));

        return ResponseEntity.ok(body);
    }
}
