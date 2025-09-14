package com.pnh.controllers;

import com.pnh.pojo.Rooms;
import com.pnh.services.RoomService;
import com.pnh.services.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reception")
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

    @PostMapping("/booking_manage/updateStatus")
    public String updateStatus(@RequestParam("id") Long roomId,
            @RequestParam("status") String status,
            Model model) {
        int updated = this.roomService.updateStatusById(roomId, status);
        if (updated > 0) {
            return "redirect:/reception/booking_manage";
        } else {
            model.addAttribute("error", "Không tìm thấy phòng để cập nhật!");
            model.addAttribute("rooms", this.roomService.getRooms(null));
            model.addAttribute("roomTypes", this.roomTypeService.getRoomTypes());
            return "booking_manage";
        }
    }

}