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

@Controller
public class BookingManagerController {
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
    
    
    @GetMapping("/reception/rooms/{id}/edit")
    public String editRoom(@PathVariable("id") Long id, Model model) {
        Rooms room = this.roomService.getRoomById(id);
        if (room == null) {
            return "booking_manage";
        }
        model.addAttribute("room", room);
        model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
        return "edit_booking";
    }
    
    @PostMapping("/reception/rooms")
    public String updateRoomStatus(@ModelAttribute Rooms room,Model model) {
    Rooms existingRoom = roomService.getRoomById(room.getId());
    if (existingRoom != null) {
        existingRoom.setStatus(room.getStatus()); // chỉ update status
        roomService.addOrUpdate(existingRoom);
    }
    model.addAttribute("rooms", this.roomService.getRooms(null));
    model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
    return "booking_manage";
}
}
