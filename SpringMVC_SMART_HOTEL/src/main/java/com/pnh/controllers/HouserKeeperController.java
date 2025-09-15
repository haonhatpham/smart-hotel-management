/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.controllers;

import com.pnh.pojo.Rooms;
import com.pnh.services.RoomService;
import com.pnh.services.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author User
 */

@Controller
@RequestMapping("/housekeeping")
public class HouserKeeperController {
     @Autowired
    private RoomTypeService roomTypeService;
    
    @Autowired
    private RoomService roomService;
    @GetMapping("/booking_manage")
    public String bookingManage(Model model) {
        model.addAttribute("rooms", this.roomService.getRooms(null));
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        return "booking_manage"; 
    }
    
    @PostMapping("/booking_manage/updateStatus")
    public String updateStatus(@RequestParam("id") Long roomId,
            @RequestParam("status") String status,
            Model model) {
        int updated = this.roomService.updateStatusById(roomId, status);
        if (updated > 0) {
            return "redirect:/housekeeping/booking_manage";
        } else {
            model.addAttribute("error", "Không tìm thấy phòng để cập nhật!");
            model.addAttribute("rooms", this.roomService.getRooms(null));
            model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
            return "booking_manage";
        }
    }
}