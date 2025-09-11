package com.pnh.services;

import com.pnh.pojo.Invoices;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.Reservations;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationService {

    Reservations getById(Long id);
    Reservations addOrUpdate(Reservations reservation);
    Reservations updateStatus(Long id, String status);
    void replaceReservationRooms(Long reservationId, List<ReservationRooms> items);
    Invoices addOrUpdateInvoice(Invoices invoice);
    boolean hasOverlapForAnyRoom(List<Long> roomIds, LocalDate checkIn, LocalDate checkOut, List<String> statuses);

    List<Reservations> getReservations(Map<String, String> params);
}