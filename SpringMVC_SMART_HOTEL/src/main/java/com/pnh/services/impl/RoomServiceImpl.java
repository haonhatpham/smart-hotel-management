/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.pojo.Rooms;
import com.pnh.repositories.RoomRepository;
import com.pnh.services.RoomService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    
    @Override
    public List<Rooms> getRooms(Map<String, String> params) {
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
