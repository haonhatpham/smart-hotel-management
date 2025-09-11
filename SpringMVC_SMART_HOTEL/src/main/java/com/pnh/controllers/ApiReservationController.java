package com.pnh.controllers;

import com.pnh.pojo.Reservations;
import com.pnh.services.ReservationService;
import java.security.Principal;
import java.util.HashMap;
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

	@GetMapping("/reservations")
	public ResponseEntity<Map<String, Object>> list(
			Principal principal,
			@RequestParam(name = "page", required = false, defaultValue = "1") int page,
			@RequestParam(name = "size", required = false, defaultValue = "6") int size,
			@RequestParam(name = "status", required = false) String status
	) {
		// Ở đây bạn có thể map username -> userId qua UserService nếu cần
		// Tạm thời giả định client gửi kèm customerId trong header/param khác nếu chưa có Principal
		Map<String, String> params = new HashMap<>();
		// params.put("customerId", currentUserId.toString()); // cần bổ sung khi có user hiện hành
		params.put("page", String.valueOf(page));
		params.put("size", String.valueOf(size));
		if (status != null && !status.isEmpty()) params.put("status", status);

		List<Reservations> items = this.reservationService.getReservations(params);
		Map<String, Object> body = new HashMap<>();
		body.put("items", items);
		body.put("page", page);
		body.put("size", size);
		return new ResponseEntity<>(body, HttpStatus.OK);
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
