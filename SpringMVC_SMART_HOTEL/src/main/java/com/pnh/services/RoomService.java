/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services;

import com.pnh.pojo.Rooms;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface RoomService {

    List<Rooms> getRooms(Map<String, String> params);

    Rooms getRoomById(Long id);

    Rooms getRoomByNumber(String roomNumber);
    
    boolean existsByRoomNumber(String roomNumber);
    
    void addOrUpdate(Rooms room);
    
    void deleteRoom(Long id);

    boolean isRoomFree(Long roomId, LocalDate checkIn, LocalDate checkOut);
    
    List<Rooms> findByRoomTypeId(Long roomTypeId);

    List<Rooms> findByStatus(String status);

    int updateStatusByIds(List<Long> ids, String status);

    long countByStatus(String status);
    

    long countByRoomType(Long roomTypeId);
}
