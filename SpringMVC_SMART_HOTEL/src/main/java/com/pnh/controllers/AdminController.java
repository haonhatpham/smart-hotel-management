/*
 * Admin management controller
 */
package com.pnh.controllers;

import com.pnh.pojo.Rooms;
import com.pnh.pojo.RoomTypes;
import com.pnh.pojo.Services;
import com.pnh.services.RoomTypeService;
import com.pnh.services.RoomService;
import com.pnh.services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage")
public class AdminController {

    @Autowired
    private RoomTypeService roomTypeService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ServiceService serviceService;

    // ============ ROOMS ============
    @GetMapping("/rooms")
    public String manageRooms(Model model) {
        java.util.Map<String, String> params = new java.util.HashMap<>();
        params.put("userRole", "ADMIN");
        model.addAttribute("rooms", this.roomService.getRooms(params));
        return "manage-rooms";
    }

    @GetMapping("/rooms/add")
    public String addRoomView(Model model) {
        model.addAttribute("room", new Rooms());
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        return "manage-room";
    }
    
    @PostMapping("/rooms/add")
    public String addRoom(@ModelAttribute(value = "room") Rooms room, Model model) {
        try {
            this.roomService.addOrUpdate(room);
            return "redirect:/manage/rooms";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
            return "manage-room";
        }
    }
    
    @GetMapping("/rooms/{roomId}")
    public String updateRoomView(Model model, @PathVariable(value = "roomId") Long id) {
        Rooms room = this.roomService.getRoomById(id);
        if (room == null) {
            return "redirect:/manage/rooms";
        }
        model.addAttribute("room", room);
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        return "manage-room";
    }
    
    @PostMapping("/rooms/{roomId}/delete")
    public String deleteRoom(@PathVariable(value = "roomId") Long id) {
        this.roomService.deleteRoom(id);
        return "redirect:/manage/rooms";
    }

    // ============ ROOM TYPES ============
    @GetMapping("/room-types")
    public String listRoomTypes(Model model) {
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        model.addAttribute("roomType", new RoomTypes());
        return "manage-room-types";
    }

    @GetMapping("/room-types/add")
    public String addRoomTypeView(Model model) {
        model.addAttribute("roomType", new RoomTypes());
        return "manage-room-type";
    }

    @PostMapping("/room-types/add")
    public String addRoomType(@ModelAttribute("roomType") RoomTypes roomType, Model model) {
        try {
            this.roomTypeService.addOrUpdate(roomType);
            return "redirect:/manage/room-types";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "manage-room-type";
        }
    }

    @GetMapping("/room-types/{id}")
    public String editRoomTypeView(@PathVariable("id") Long id, Model model) {
        RoomTypes rt = this.roomTypeService.getById(id);
        if (rt == null) return "redirect:/manage/room-types";
        model.addAttribute("roomType", rt);
        return "manage-room-type";
    }

    @PostMapping("/room-types/{id}/delete")
    public String deleteRoomType(@PathVariable("id") Long id) {
        this.roomTypeService.delete(id);
        return "redirect:/manage/room-types";
    }

    // ============ SERVICES ============
    @GetMapping("/services")
    public String listServices(Model model) {
        model.addAttribute("services", this.serviceService.getServices());
        model.addAttribute("service", new Services());
        return "manage-services";
    }

    @GetMapping("/services/add")
    public String addServiceView(Model model) {
        model.addAttribute("service", new Services());
        return "manage-service";
    }

    @PostMapping("/services/add")
    public String addService(@ModelAttribute("service") Services service, Model model) {
        try {
            this.serviceService.addOrUpdate(service);
            return "redirect:/manage/services";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "manage-service";
        }
    }

    @GetMapping("/services/{id}")
    public String editServiceView(@PathVariable("id") Long id, Model model) {
        Services sv = this.serviceService.getById(id);
        if (sv == null) return "redirect:/manage/services";
        model.addAttribute("service", sv);
        return "manage-service";
    }

    @PostMapping("/services/{id}/delete")
    public String deleteService(@PathVariable("id") Long id) {
        this.serviceService.delete(id);
        return "redirect:/manage/services";
    }
}


