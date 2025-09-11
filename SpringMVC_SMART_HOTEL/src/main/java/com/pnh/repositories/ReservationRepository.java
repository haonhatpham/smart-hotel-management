/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.repositories;

import com.pnh.pojo.Invoices;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.Reservations;
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
    void replaceReservationRooms(Long reservationId, List<ReservationRooms> items);

    Invoices getInvoiceByReservationId(Long reservationId);
    Invoices addOrUpdateInvoice(Invoices invoice);

    boolean hasOverlapForAnyRoom(List<Long> roomIds, java.time.LocalDate checkIn, java.time.LocalDate checkOut, List<String> statuses);

    List<Reservations> getReservations(Map<String, String> params);
}
