/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.controllers;

import com.pnh.services.RoomService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.pnh.services.RoomTypeService;

/**
 *
 * @author ADMIN
 */
@Controller
@ControllerAdvice
public class IndexController {
    
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomTypeService roomTypeService;
    
    @ModelAttribute
    public void commonResponse(Model model) {
        // Thống kê cơ bản cho admin
        model.addAttribute("totalRooms", this.roomService.countByStatus(null));
        model.addAttribute("availableRooms", this.roomService.countByStatus("AVAILABLE"));
        model.addAttribute("totalReservations", 0); // Tạm thời set 0
        model.addAttribute("totalCustomers", 0); // Tạm thời set 0
    }
    
    @RequestMapping("/")
    public String index(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("rooms", this.roomService.getRooms(params));
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes() );
        return "index";
    }
    
    @RequestMapping("/rooms")
    public String rooms(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("rooms", this.roomService.getRooms(params));
        model.addAttribute("params", params);
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        return "search";
    }

    @RequestMapping("/rooms/{id}")
    public String roomDetail(Model model, @PathVariable("id") Long id) {
        model.addAttribute("room", this.roomService.getRoomById(id));
        return "room-detail";
    }

}
