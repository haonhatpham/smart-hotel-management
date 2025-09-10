/*
 * API Controller for Rooms - Client Side Only
 */
package com.pnh.controllers;

import com.pnh.pojo.Rooms;
import com.pnh.pojo.Reviews;
import com.pnh.services.RoomService;
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
}
