/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.pojo.Invoices;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.Reservations;
import com.pnh.repositories.ReservationRepository;
import com.pnh.services.ReservationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Reservations getById(Long id) {
        return this.reservationRepository.getById(id);
    }

    @Override
    public Reservations addOrUpdate(Reservations reservation) {
        return this.reservationRepository.addOrUpdate(reservation);
    }

    @Override
    public Reservations updateStatus(Long id, String status) {
        return this.reservationRepository.updateStatus(id, status);
    }

    @Override
    public void replaceReservationRooms(Long reservationId, List<ReservationRooms> items) {
        this.reservationRepository.replaceReservationRooms(reservationId, items);
    }

    @Override
    public Invoices addOrUpdateInvoice(Invoices invoice) {
        return this.reservationRepository.addOrUpdateInvoice(invoice);
    }

    @Override
    public boolean hasOverlapForAnyRoom(List<Long> roomIds, LocalDate checkIn, LocalDate checkOut, List<String> statuses) {
        return this.reservationRepository.hasOverlapForAnyRoom(roomIds, checkIn, checkOut, statuses);
    }

    @Override
    public List<Reservations> getReservations(Map<String, String> params) {
        return this.reservationRepository.getReservations(params);
    }
}