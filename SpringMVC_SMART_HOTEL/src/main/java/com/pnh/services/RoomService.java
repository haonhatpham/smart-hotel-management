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

    boolean existsByRoomNumber(String roomNumber);
    
    void addOrUpdate(Rooms room);
    
    void deleteRoom(Long id);


    int updateStatusById(Long roomId, String status);


    long countByStatus(String status);
    

    long countByRoomType(Long roomTypeId);
}
