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

    ReservationRooms addOrUpdateReservationRoom(ReservationRooms reservationRoom);
    ServiceOrders addOrUpdateServiceOrder(ServiceOrders serviceOrder) ;
    
    List<ReservationRooms> getReservationsRoomByReservationsId(Long id);
}
