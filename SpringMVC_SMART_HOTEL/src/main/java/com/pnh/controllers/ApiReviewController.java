package com.pnh.controllers;

import com.pnh.pojo.Reservations;
import com.pnh.pojo.Reviews;
import com.pnh.services.ReservationService;
import com.pnh.services.ReviewService;
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

    @GetMapping("/reservations/{id}/review")
    public ResponseEntity<?> getOne(@PathVariable("id") Long id) {
        Reviews r = this.reviewService.getByReservationId(id);
        if (r == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @PostMapping("/reservations/{id}/review")
    public ResponseEntity<?> create(@PathVariable("id") Long id, @RequestBody Reviews payload) {
        Reviews existing = this.reviewService.getByReservationId(id);
        if (existing != null) return new ResponseEntity<>("Review đã tồn tại", HttpStatus.CONFLICT);

        Reservations res = this.reservationService.getById(id);
        if (res == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

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


