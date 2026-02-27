package com.pnh.controllers;

import com.pnh.pojo.Reservations;
import com.pnh.pojo.Reviews;
import com.pnh.pojo.Users;
import com.pnh.services.ReservationService;
import com.pnh.services.ReviewService;
import com.pnh.services.UserService;
import java.security.Principal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    private boolean canAccessReservation(Reservations r, Users currentUser) {
        if (currentUser == null || r == null || r.getCustomerId() == null) return false;
        return r.getCustomerId().getId().equals(currentUser.getId());
    }

    @GetMapping("/secure/reservations/{id}/review")
    public ResponseEntity<?> getOne(Principal principal, @PathVariable("id") Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }
        Users currentUser = userService.getUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy người dùng");
        }
        Reservations res = reservationService.getById(id);
        if (res == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (!canAccessReservation(res, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền xem đánh giá đơn này");
        }
        Reviews r = this.reviewService.getByReservationId(id);
        if (r == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @PostMapping("/secure/reservations/{id}/review")
    public ResponseEntity<?> create(Principal principal, @PathVariable("id") Long id, @RequestBody Reviews payload) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }
        Users currentUser = userService.getUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy người dùng");
        }
        Reservations res = this.reservationService.getById(id);
        if (res == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (!canAccessReservation(res, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền đánh giá đơn này");
        }
        Reviews existing = this.reviewService.getByReservationId(id);
        if (existing != null) return new ResponseEntity<>("Review đã tồn tại", HttpStatus.CONFLICT);

        Reviews r = new Reviews();
        r.setReservationId(res);
        r.setRating(payload.getRating());
        r.setComment(payload.getComment());
        r.setVisible(true);
        r.setCreatedAt(new Date());
        Reviews saved = this.reviewService.addOrUpdate(r);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}


