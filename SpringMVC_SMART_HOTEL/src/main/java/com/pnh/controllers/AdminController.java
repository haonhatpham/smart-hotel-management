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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    private RoomTypeService roomTypeService;
    @Autowired
    private RoomService roomService;

    @GetMapping("/admin")
    public String adminRoomsTable(Model model) {
        model.addAttribute("rooms", this.roomService.getRooms(java.util.Collections.emptyMap()));
        return "admin-rooms-table";
    }

    @GetMapping("/admin/rooms")
    public String manageRooms(Model model) {
        model.addAttribute("room", new Rooms());
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        return "admin-rooms";
    }

    @PostMapping("/admin/rooms")
    public String saveRoom(@ModelAttribute Rooms room) {
        // TODO: save via service once wired in
        return "redirect:/admin";
    }

    @GetMapping("/admin/rooms/{id}/edit")
    public String editRoom(@PathVariable("id") Long id, Model model) {
        Rooms room = this.roomService.getRoomById(id);
        if (room == null) {
            return "redirect:/admin";
        }
        model.addAttribute("room", room);
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        return "admin-rooms";
    }

    @PostMapping("/admin/rooms/{id}/delete")
    public String deleteRoom(@PathVariable("id") Long id, @RequestParam(name="confirm", required=false) String confirm) {
        // TODO: delete via service once wired in
        return "redirect:/admin";
    }
}


