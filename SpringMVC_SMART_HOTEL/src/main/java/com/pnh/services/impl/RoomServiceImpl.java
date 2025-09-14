/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pnh.pojo.Rooms;
import com.pnh.repositories.RoomRepository;
import com.pnh.services.RoomService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 *
 * @author ADMIN
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<Rooms> getRooms(Map<String, String> params) {
        if (params == null) {
            params = new java.util.HashMap<>();
        }

        // Lấy role hiện tại từ Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            
            // Debug: In ra role để kiểm tra
            System.out.println("Current user role: " + role);

            if ("ROLE_CUSTOMER".equals(role)) {
                params.put("status", "AVAILABLE");
            } else {
                params.remove("status"); 
            }
        }

        return this.roomRepository.getRooms(params);
    }

    @Override
    public Rooms getRoomById(Long id) {
        return this.roomRepository.getRoomById(id);
    }

    @Override
    public boolean existsByRoomNumber(String roomNumber) {
        return this.roomRepository.existsByRoomNumber(roomNumber);
    }

    @Override
    public void addOrUpdate(Rooms room) {
        if (room.getId() == null) {
            if (this.roomRepository.existsByRoomNumber(room.getRoomNumber())) {
                throw new RuntimeException("Số phòng '" + room.getRoomNumber() + "' đã tồn tại!");
            }
        } else {
            Rooms existingRoom = this.roomRepository.getRoomById(room.getId());
            if (existingRoom != null && !existingRoom.getRoomNumber().equals(room.getRoomNumber())) {
                if (this.roomRepository.existsByRoomNumber(room.getRoomNumber())) {
                    throw new RuntimeException("Số phòng '" + room.getRoomNumber() + "' đã tồn tại!");
                }
            }
        }

        if (room.getFile() != null && !room.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(room.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                room.setImageUrl(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(RoomServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (room.getId() != null) {
            Rooms existingRoom = this.roomRepository.getRoomById(room.getId());
            if (existingRoom != null && existingRoom.getImageUrl() != null) {
                room.setImageUrl(existingRoom.getImageUrl());
            }
        }
        this.roomRepository.addOrUpdate(room);
    }

    @Override
    public void deleteRoom(Long id) {
        this.roomRepository.deleteRoom(id);
    }

    @Override
    public int updateStatusById(Long roomId, String status) {
        return this.roomRepository.updateStatusById(roomId, status);
    }

    @Override
    public long countByStatus(String status) {
        return this.roomRepository.countByStatus(status);
    }

    @Override
    public long countByRoomType(Long roomTypeId) {
        return this.roomRepository.countByRoomType(roomTypeId);
    }

}
