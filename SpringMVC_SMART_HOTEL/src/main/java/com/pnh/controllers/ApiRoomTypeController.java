/*
 * API Controller for Room Types - Client Side Only
 */
package com.pnh.controllers;

import com.pnh.pojo.RoomTypes;
import com.pnh.services.RoomTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiRoomTypeController {
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @GetMapping("/room-types")
    public ResponseEntity<List<RoomTypes>> list() {
        return new ResponseEntity<>(this.roomTypeService.getRoomTypes(), HttpStatus.OK);
    }
    
    // Method getRoomTypeById không tồn tại trong RoomTypeService
    // Tạm thời comment để tránh lỗi
    /*
    @GetMapping("/room-types/{id}")
    public ResponseEntity<RoomTypes> retrieve(@PathVariable(value = "id") Long id) {
        RoomTypes roomType = this.roomTypeService.getRoomTypeById(id);
        if (roomType != null) {
            return new ResponseEntity<>(roomType, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    */
}
