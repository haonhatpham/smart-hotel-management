/*
 * API Controller for Rooms - Client Side Only
 */
package com.pnh.controllers;

import com.pnh.pojo.Rooms;
import com.pnh.pojo.Reviews;
import com.pnh.services.RoomService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiRoomController {
    
    @Autowired
    private RoomService roomService;
    
    @GetMapping("/rooms")
    public ResponseEntity<List<Rooms>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.roomService.getRooms(params), HttpStatus.OK);
    }
    
    @GetMapping("/rooms/{id}")
    public ResponseEntity<Rooms> retrieve(@PathVariable(value = "id") Long id) {
        Rooms room = this.roomService.getRoomById(id);
        if (room != null) {
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Gợi ý khoảng ngày gần nhất còn phòng nếu khoảng người dùng chọn đã hết phòng.
     * Param bắt buộc: checkIn, checkOut
     * Param tùy chọn: minCapacity, roomTypeId
     */
    @GetMapping("/rooms/nearest-available")
    public ResponseEntity<?> suggestNearestAvailable(
            @RequestParam Map<String, String> params
    ) {
        String checkInStr = params.get("checkIn");
        String checkOutStr = params.get("checkOut");

        if (checkInStr == null || checkOutStr == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Thiếu tham số checkIn hoặc checkOut"
            ));
        }

        try {
            LocalDate checkIn = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);

            Integer minCapacity = null;
            Long roomTypeId = null;

            String minCapacityStr = params.get("minCapacity");
            if (minCapacityStr != null && !minCapacityStr.isEmpty()) {
                minCapacity = Integer.parseInt(minCapacityStr);
            }

            String roomTypeIdStr = params.get("roomTypeId");
            if (roomTypeIdStr != null && !roomTypeIdStr.isEmpty()) {
                roomTypeId = Long.parseLong(roomTypeIdStr);
            }

            var range = this.roomService.findNearestAvailableRange(checkIn, checkOut, minCapacity, roomTypeId, 30);
            if (range == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "Không tìm thấy khoảng ngày phù hợp trong thời gian gần"
                ));
            }

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("checkIn", range[0].toString());
            body.put("checkOut", range[1].toString());
            return ResponseEntity.ok(body);
        } catch (DateTimeParseException | NumberFormatException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Tham số không hợp lệ: " + ex.getMessage()
            ));
        }
    }
}
