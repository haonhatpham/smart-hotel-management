/*
 * Admin management controller
 */
package com.pnh.controllers;

import com.pnh.pojo.Rooms;
import com.pnh.services.RoomTypeService;
import com.pnh.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AdminController {

    @Autowired
    private RoomTypeService roomTypeService;
    @Autowired
    private RoomService roomService;

    @GetMapping("/manage")
    public String manageRooms(Model model) {
        java.util.Map<String, String> params = new java.util.HashMap<>();
        params.put("userRole", "ADMIN");
        model.addAttribute("rooms", this.roomService.getRooms(params));
        return "manage";
    }

    @GetMapping("/manage/add")
    public String addView(Model model) {
        model.addAttribute("room", new Rooms());
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        return "manage-rooms";
    }
    
    @PostMapping("/manage/add")
    public String add(@ModelAttribute(value = "room") Rooms room, Model model) {
        try {
            this.roomService.addOrUpdate(room);
            return "redirect:/manage";
        } catch (RuntimeException e) {
            // Hiển thị thông báo lỗi và quay lại form
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
            return "manage-rooms";
        }
    }
    
    @GetMapping("manage/rooms/{roomId}")
    public String updateView(Model model, @PathVariable(value = "roomId") Long id) {
        Rooms room = this.roomService.getRoomById(id);
        if (room == null) {
            return "redirect:/manage";
        }
        model.addAttribute("room", room);
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        return "manage-rooms";
    }
    
    @PostMapping("manage/rooms/{roomId}/delete")
    public String deleteRoom(@PathVariable(value = "roomId") Long id) {
        this.roomService.deleteRoom(id);
        return "redirect:/manage";
    }
}


