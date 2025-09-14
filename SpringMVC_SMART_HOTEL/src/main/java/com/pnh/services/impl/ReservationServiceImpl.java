/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.dto.ReservationCreateDTO;
import com.pnh.dto.ReservationRoomDTO;
import com.pnh.dto.ServiceOrderDTO;
import com.pnh.pojo.Invoices;
import com.pnh.pojo.ReservationRooms;
import com.pnh.pojo.ServiceOrders;
import com.pnh.pojo.Reservations;
import com.pnh.pojo.Rooms;
import com.pnh.pojo.Services;
import com.pnh.pojo.Users;
import com.pnh.repositories.ReservationRepository;
import com.pnh.services.ReservationService;
import com.pnh.services.RoomService;
import com.pnh.services.ServiceService;
import com.pnh.services.UserService;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private ServiceService serviceService;

    @Override
    public Reservations getById(Long id) {
        return this.reservationRepository.getById(id);
    }

    @Override
    public Reservations addOrUpdate(Reservations reservation) {
        System.out.println("[DEBUG] ReservationServiceImpl.addOrUpdate called");
        System.out.println("[DEBUG] ReservationRoomsSet size: " + 
            (reservation.getReservationRoomsSet() != null ? reservation.getReservationRoomsSet().size() : "null"));
        System.out.println("[DEBUG] ServiceOrdersSet size: " + 
            (reservation.getServiceOrdersSet() != null ? reservation.getServiceOrdersSet().size() : "null"));
        
        if (reservation.getReservationRoomsSet() != null) {
            System.out.println("[DEBUG] Processing ReservationRooms...");
            for (ReservationRooms rr : reservation.getReservationRoomsSet()) {
                rr.setReservationId(reservation);
                System.out.println("[DEBUG] Set ReservationRoom: roomId=" + rr.getRoomId() + ", price=" + rr.getPricePerNight());
            }
        }

        if (reservation.getServiceOrdersSet() != null) {
            System.out.println("[DEBUG] Processing ServiceOrders...");
            for (ServiceOrders so : reservation.getServiceOrdersSet()) {
                so.setReservationId(reservation);
                System.out.println("[DEBUG] Set ServiceOrder: serviceId=" + so.getServiceId() + ", qty=" + so.getQty());
            }
        }

        Reservations result = this.reservationRepository.addOrUpdate(reservation);
        System.out.println("[DEBUG] Saved reservation with ID: " + result.getId());
        return result;
    }

    @Override
    public Reservations createFromDTO(ReservationCreateDTO dto) {
        System.out.println("[DEBUG] ReservationServiceImpl.createFromDTO called");
        System.out.println("[DEBUG] DTO: " + dto);
        
        // Tạo reservation mới
        Reservations reservation = new Reservations();
        
        // Tính check_in và check_out tổng quát từ các phòng
        Date earliestCheckIn = null;
        Date latestCheckOut = null;
        
        if (dto.getRooms() != null && !dto.getRooms().isEmpty()) {
            for (ReservationRoomDTO roomDTO : dto.getRooms()) {
                if (earliestCheckIn == null || roomDTO.getCheckIn().before(earliestCheckIn)) {
                    earliestCheckIn = roomDTO.getCheckIn();
                }
                if (latestCheckOut == null || roomDTO.getCheckOut().after(latestCheckOut)) {
                    latestCheckOut = roomDTO.getCheckOut();
                }
            }
        }
        
        // Nếu không có phòng nào, sử dụng giá trị từ DTO
        if (earliestCheckIn == null) {
            earliestCheckIn = dto.getCheckIn();
        }
        if (latestCheckOut == null) {
            latestCheckOut = dto.getCheckOut();
        }
        
        reservation.setCheckIn(earliestCheckIn);
        reservation.setCheckOut(latestCheckOut);
        reservation.setStatus(dto.getStatus() != null ? dto.getStatus() : "HELD");
        reservation.setCreatedAt(new Date());
        
        // Lấy customer
        Users customer = this.userService.getById(dto.getCustomerId());
        if (customer == null) {
            throw new RuntimeException("Customer not found with ID: " + dto.getCustomerId());
        }
        reservation.setCustomerId(customer);
        
        // Lưu reservation trước để có ID
        Reservations savedReservation = this.reservationRepository.addOrUpdate(reservation);
        System.out.println("[DEBUG] Saved reservation with ID: " + savedReservation.getId());
        
        // Tạo và lưu ReservationRooms
        if (dto.getRooms() != null) {
            for (ReservationRoomDTO roomDTO : dto.getRooms()) {
                Rooms room = this.roomService.getRoomById(roomDTO.getRoomId());
                if (room == null) {
                    throw new RuntimeException("Room not found with ID: " + roomDTO.getRoomId());
                }
                
                ReservationRooms reservationRoom = new ReservationRooms();
                reservationRoom.setRoomId(room);
                reservationRoom.setCheckIn(roomDTO.getCheckIn());
                reservationRoom.setCheckOut(roomDTO.getCheckOut());
                reservationRoom.setPricePerNight(roomDTO.getPricePerNight());
                reservationRoom.setNotes(roomDTO.getNotes());
                reservationRoom.setReservationId(savedReservation);
                
                // Lưu từng ReservationRoom riêng biệt
                this.reservationRepository.addOrUpdateReservationRoom(reservationRoom);
                System.out.println("[DEBUG] Saved ReservationRoom: roomId=" + roomDTO.getRoomId() + ", price=" + roomDTO.getPricePerNight());
            }
        }
        
        // Tạo và lưu ServiceOrders
        if (dto.getServices() != null) {
            for (ServiceOrderDTO serviceDTO : dto.getServices()) {
                Services service = this.serviceService.getById(serviceDTO.getServiceId());
                if (service == null) {
                    throw new RuntimeException("Service not found with ID: " + serviceDTO.getServiceId());
                }
                
                ServiceOrders serviceOrder = new ServiceOrders();
                serviceOrder.setServiceId(service);
                serviceOrder.setQty(serviceDTO.getQty());
                serviceOrder.setUnitPrice(serviceDTO.getUnitPrice());
                serviceOrder.setAmount(serviceDTO.getAmount());
                serviceOrder.setOrderedAt(serviceDTO.getOrderedAt() != null ? serviceDTO.getOrderedAt() : new Date());
                serviceOrder.setNotes(serviceDTO.getNotes());
                serviceOrder.setReservationId(savedReservation);
                
                // Lưu từng ServiceOrder riêng biệt
                this.reservationRepository.addOrUpdateServiceOrder(serviceOrder);
                System.out.println("[DEBUG] Saved ServiceOrder: serviceId=" + serviceDTO.getServiceId() + ", qty=" + serviceDTO.getQty());
            }
        }
        
        return savedReservation;
    }

    @Override
    public Reservations updateStatus(Long id, String status) {
        return this.reservationRepository.updateStatus(id, status);
    }



    @Override
    public List<Reservations> getReservations(Map<String, String> params) {
        return this.reservationRepository.getReservations(params);
    }

    @Override
    public List<ReservationRooms> getReservationRooms(Long reservationId) {
        return this.reservationRepository.getReservationRooms(reservationId);
    }

    @Override
    public List<ServiceOrders> getServiceOrders(Long reservationId) {
        return this.reservationRepository.getServiceOrders(reservationId);
    }

    @Override
    public Invoices getInvoiceByReservationId(Long reservationId) {
        return this.reservationRepository.getInvoiceByReservationId(reservationId);
    }

    @Override
    public void replaceReservationRooms(Long reservationId, List<ReservationRooms> items) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Invoices addOrUpdateInvoice(Invoices invoice) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean hasOverlapForAnyRoom(List<Long> roomIds, LocalDate checkIn, LocalDate checkOut, List<String> statuses) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
