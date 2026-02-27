/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories;

import com.pnh.pojo.Invoices;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.Reservations;
import com.pnh.pojo.ServiceOrders;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository cho nghiệp vụ đặt phòng
 */
public interface ReservationRepository {
    Reservations getById(Long id);
    Reservations addOrUpdate(Reservations reservation);
    Reservations updateStatus(Long id, String status);
    List<ReservationRooms> getReservationRooms(Long reservationId);

    Invoices getInvoiceByReservationId(Long reservationId);

    List<ServiceOrders> getServiceOrders(Long reservationId);

    List<Reservations> getReservations(Map<String, String> params);

    long countReservations(Map<String, String> params);

    ReservationRooms addOrUpdateReservationRoom(ReservationRooms reservationRoom);
    ServiceOrders addOrUpdateServiceOrder(ServiceOrders serviceOrder) ;
    
    List<ReservationRooms> getReservationsRoomByReservationsId(Long id);

    /**
     * Kiểm tra phòng đã có đặt trùng khoảng ngày (đơn không hủy).
     * Trùng: tồn tại reservation_room cùng roomId, reservation.status != CANCELLED,
     * và (existing.check_in < checkOut && existing.check_out > checkIn).
     */
    boolean existsOverlappingBooking(Long roomId, java.util.Date checkIn, java.util.Date checkOut);

    /**
     * Lấy danh sách id loại phòng mà khách này đã đặt nhiều nhất (ưu tiên lịch sử gần đây).
     */
    List<Long> findTopRoomTypeIdsByCustomer(Long customerId, int limit);

    /**
     * Lấy danh sách id dịch vụ mà khách này thường sử dụng nhiều nhất.
     */
    List<Long> findTopServiceIdsByCustomer(Long customerId, int limit);
}
