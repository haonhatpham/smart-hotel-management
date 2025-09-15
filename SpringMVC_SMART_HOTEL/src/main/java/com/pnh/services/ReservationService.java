package com.pnh.services;

import com.pnh.dto.ReservationCreateDTO;
import com.pnh.pojo.Invoices;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.Reservations;
import com.pnh.pojo.ServiceOrders;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReservationService {

    Reservations getById(Long id);
    Reservations addOrUpdate(Reservations reservation);
    Reservations createFromDTO(ReservationCreateDTO dto);
    Reservations updateStatus(Long id, String status);
    List<Reservations> getReservations(Map<String, String> params);

    List<ReservationRooms> getReservationRooms(Long reservationId);
    List<ServiceOrders> getServiceOrders(Long reservationId);
    Invoices getInvoiceByReservationId(Long reservationId);
    
    List<ReservationRooms> getReservationsRoomByReservationsId(Long id);
}