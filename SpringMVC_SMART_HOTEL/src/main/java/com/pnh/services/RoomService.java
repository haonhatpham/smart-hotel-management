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

    /**
     * Tìm khoảng ngày gần nhất trong tương lai còn phòng phù hợp
     * so với khoảng check-in/check-out mong muốn.
     *
     * @param checkIn     ngày nhận phòng mong muốn
     * @param checkOut    ngày trả phòng mong muốn
     * @param minCapacity số khách tối thiểu (có thể null)
     * @param roomTypeId  loại phòng ưu tiên (có thể null)
     * @param maxDaysScan số ngày tối đa quét về phía trước
     * @return mảng 2 phần tử [checkInĐềXuất, checkOutĐềXuất] hoặc null nếu không tìm thấy
     */
    LocalDate[] findNearestAvailableRange(LocalDate checkIn, LocalDate checkOut,
                                          Integer minCapacity, Long roomTypeId, int maxDaysScan);
}
