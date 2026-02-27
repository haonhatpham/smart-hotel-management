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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.logging.Logger;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private static final Logger LOG = Logger.getLogger(ReservationServiceImpl.class.getName());

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
        if (reservation.getReservationRoomsSet() != null) {
            for (ReservationRooms rr : reservation.getReservationRoomsSet()) {
                rr.setReservationId(reservation);
            }
        }
        if (reservation.getServiceOrdersSet() != null) { 
            for (ServiceOrders so : reservation.getServiceOrdersSet()) {
                so.setReservationId(reservation);
            }
        }
        Reservations result = this.reservationRepository.addOrUpdate(reservation);
        return result;
    }

    @Override
    public Reservations createFromDTO(ReservationCreateDTO dto) {
        // --- Validation: phải có ít nhất một phòng ---
        if (dto.getRooms() == null || dto.getRooms().isEmpty()) {
            throw new IllegalArgumentException("Phải chọn ít nhất một phòng.");
        }

        Date earliestCheckIn = null;
        Date latestCheckOut = null;
        for (ReservationRoomDTO roomDTO : dto.getRooms()) {
            Date ci = roomDTO.getCheckIn();
            Date co = roomDTO.getCheckOut();
            if (ci == null || co == null) {
                throw new IllegalArgumentException("Ngày nhận phòng và ngày trả phòng không được để trống.");
            }
            if (!ci.before(co)) {
                throw new IllegalArgumentException("Ngày nhận phòng phải trước ngày trả phòng.");
            }
            if (earliestCheckIn == null || ci.before(earliestCheckIn)) earliestCheckIn = ci;
            if (latestCheckOut == null || co.after(latestCheckOut)) latestCheckOut = co;
        }
        if (earliestCheckIn == null) earliestCheckIn = dto.getCheckIn();
        if (latestCheckOut == null) latestCheckOut = dto.getCheckOut();
        if (earliestCheckIn == null || latestCheckOut == null) {
            throw new IllegalArgumentException("Thiếu thông tin ngày nhận/trả phòng.");
        }
        if (!earliestCheckIn.before(latestCheckOut)) {
            throw new IllegalArgumentException("Ngày nhận phòng phải trước ngày trả phòng.");
        }

        // Ngày nhận phòng không được trong quá khứ (cho phép từ đầu ngày hôm nay)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfToday = cal.getTime();
        if (earliestCheckIn.before(startOfToday)) {
            throw new IllegalArgumentException("Ngày nhận phòng không được trong quá khứ.");
        }

        // Kiểm tra từng phòng có bị trùng lịch không
        for (ReservationRoomDTO roomDTO : dto.getRooms()) {
            Rooms room = this.roomService.getRoomById(roomDTO.getRoomId());
            if (room == null) {
                throw new IllegalArgumentException("Không tìm thấy phòng với ID: " + roomDTO.getRoomId());
            }
            if (this.reservationRepository.existsOverlappingBooking(roomDTO.getRoomId(), roomDTO.getCheckIn(), roomDTO.getCheckOut())) {
                throw new IllegalArgumentException("Phòng " + (room.getRoomNumber() != null ? room.getRoomNumber() : roomDTO.getRoomId()) + " đã được đặt trong khoảng ngày này. Vui lòng chọn phòng hoặc ngày khác.");
            }
        }

        // --- Tạo đơn ---
        Reservations reservation = new Reservations();
        reservation.setCheckIn(earliestCheckIn);
        reservation.setCheckOut(latestCheckOut);
        reservation.setStatus(dto.getStatus() != null ? dto.getStatus() : "HELD");
        reservation.setCreatedAt(new Date());
        reservation.setCreatedBy(dto.getCreatedBy() != null ? this.userService.getById(dto.getCreatedBy()) : null);

        Users customer = this.userService.getById(dto.getCustomerId());
        if (customer == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + dto.getCustomerId());
        }
        reservation.setCustomerId(customer);

        Reservations savedReservation = this.reservationRepository.addOrUpdate(reservation);
        LOG.fine("Saved reservation with ID: " + savedReservation.getId());

        for (ReservationRoomDTO roomDTO : dto.getRooms()) {
            Rooms room = this.roomService.getRoomById(roomDTO.getRoomId());
            if (room == null) {
                throw new IllegalArgumentException("Không tìm thấy phòng với ID: " + roomDTO.getRoomId());
            }

            ReservationRooms reservationRoom = new ReservationRooms();
            reservationRoom.setRoomId(room);
            reservationRoom.setCheckIn(roomDTO.getCheckIn());
            reservationRoom.setCheckOut(roomDTO.getCheckOut());
            reservationRoom.setPricePerNight(roomDTO.getPricePerNight());
            reservationRoom.setNotes(roomDTO.getNotes());
            reservationRoom.setReservationId(savedReservation);

            this.reservationRepository.addOrUpdateReservationRoom(reservationRoom);
        }

        // Tạo và lưu ServiceOrders
        if (dto.getServices() != null) {
            for (ServiceOrderDTO serviceDTO : dto.getServices()) {
                Services service = this.serviceService.getById(serviceDTO.getServiceId());
                if (service == null) {
                    throw new IllegalArgumentException("Không tìm thấy dịch vụ với ID: " + serviceDTO.getServiceId());
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
    public long countReservations(Map<String, String> params) {
        return this.reservationRepository.countReservations(params);
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
    public List<ReservationRooms> getReservationsRoomByReservationsId(Long id) {
     return this.reservationRepository.getReservationsRoomByReservationsId(id);
    }

    @Override
    public Map<String, java.util.List<Long>> getRecommendationsForCustomer(Long customerId) {
        java.util.Map<String, java.util.List<Long>> resp = new java.util.HashMap<>();
        if (customerId == null) {
            resp.put("roomTypeIds", java.util.List.of());
            resp.put("serviceIds", java.util.List.of());
            return resp;
        }

        var topRoomTypes = this.reservationRepository.findTopRoomTypeIdsByCustomer(customerId, 3);
        var topServices = this.reservationRepository.findTopServiceIdsByCustomer(customerId, 3);

        resp.put("roomTypeIds", topRoomTypes != null ? topRoomTypes : java.util.List.of());
        resp.put("serviceIds", topServices != null ? topServices : java.util.List.of());
        return resp;
    }
}
