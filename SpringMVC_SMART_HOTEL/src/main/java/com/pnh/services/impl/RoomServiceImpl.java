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

        String userRole = params.get("userRole");
        boolean isAdmin = "ADMIN".equals(userRole);

        if (!isAdmin && params.isEmpty()) {
            params.put("status", "AVAILABLE");
        }

        return this.roomRepository.getRooms(params);
    }

    @Override
    public Rooms getRoomById(Long id) {
        return this.roomRepository.getRoomById(id);
    }

    @Override
    public Rooms getRoomByNumber(String roomNumber) {
        return this.roomRepository.getRoomByNumber(roomNumber);
    }

    @Override
    public boolean existsByRoomNumber(String roomNumber) {
        return this.roomRepository.existsByRoomNumber(roomNumber);
    }

    @Override
    public void addOrUpdate(Rooms room) {
        Rooms existingRoomByNumber = this.roomRepository.getRoomByNumber(room.getRoomNumber());
        if (existingRoomByNumber != null && !existingRoomByNumber.getId().equals(room.getId())) {
            throw new RuntimeException("Số phòng '" + room.getRoomNumber() + "' đã tồn tại!");
        }
        
        // Nếu có file mới thì upload và cập nhật imageUrl
        if (room.getFile() != null && !room.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(room.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                room.setImageUrl(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(RoomServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (room.getId() != null) {
            // Nếu không có file mới và đang update, giữ nguyên imageUrl cũ
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
    public boolean isRoomFree(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return this.roomRepository.isRoomFree(roomId, checkIn, checkOut);
    }

    @Override
    public List<Rooms> findByRoomTypeId(Long roomTypeId) {
        return this.roomRepository.findByRoomTypeId(roomTypeId);
    }

    @Override
    public List<Rooms> findByStatus(String status) {
        return this.roomRepository.findByStatus(status);
    }

    @Override
    public int updateStatusByIds(List<Long> ids, String status) {
        return this.roomRepository.updateStatusByIds(ids, status);
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
