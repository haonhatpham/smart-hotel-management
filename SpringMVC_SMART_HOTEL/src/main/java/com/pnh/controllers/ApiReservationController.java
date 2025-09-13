package com.pnh.controllers;

import com.pnh.pojo.Reservations;
import com.pnh.services.ReservationService;
import com.pnh.services.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
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

	@GetMapping("/reservations")
	public ResponseEntity<List<Reservations>> list(@RequestParam Map<String, String> params) {
		return new ResponseEntity<>(this.reservationService.getReservations(params), HttpStatus.OK);
	}

	@GetMapping("/reservations/{id}")
	public ResponseEntity<Reservations> retrieve(@PathVariable(value = "id") Long id) {
		Reservations reservation = this.reservationService.getById(id);
		if (reservation != null) {
			return new ResponseEntity<>(reservation, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/reservations")
	@ResponseStatus(HttpStatus.CREATED)
	public Reservations create(@RequestBody Reservations reservation) {
		return this.reservationService.addOrUpdate(reservation);
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
