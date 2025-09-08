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
    
    /**
     * Lấy danh sách phòng với các bộ lọc
     */
    List<Rooms> getRooms(Map<String, String> params);
    
    /**
     * Lấy phòng theo ID
     */
    Rooms getRoomById(Long id);
    
    /**
     * Lấy phòng theo số phòng
     */
    Rooms getRoomByNumber(String roomNumber);
    
    /**
     * Kiểm tra số phòng đã tồn tại chưa
     */
    boolean existsByRoomNumber(String roomNumber);
    
    /**
     * Thêm hoặc cập nhật phòng
     */
    void addOrUpdate(Rooms room);
    
    /**
     * Xóa phòng theo ID
     */
    void deleteRoom(Long id);
    
    /**
     * Kiểm tra phòng có trống trong khoảng thời gian không
     */
    boolean isRoomFree(Long roomId, LocalDate checkIn, LocalDate checkOut);
    
    /**
     * Tìm phòng theo loại phòng
     */
    List<Rooms> findByRoomTypeId(Long roomTypeId);
    
    /**
     * Tìm phòng theo trạng thái
     */
    List<Rooms> findByStatus(String status);
    
    /**
     * Cập nhật trạng thái cho nhiều phòng
     */
    int updateStatusByIds(List<Long> ids, String status);
    
    /**
     * Đếm số phòng theo trạng thái
     */
    long countByStatus(String status);
    
    /**
     * Đếm số phòng theo loại phòng
     */
    long countByRoomType(Long roomTypeId);
}
