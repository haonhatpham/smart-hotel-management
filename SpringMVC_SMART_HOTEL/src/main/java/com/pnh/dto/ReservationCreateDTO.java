package com.pnh.dto;

import java.util.Date;
import java.util.List;

/**
 * DTO for creating new reservations
 */
public class ReservationCreateDTO {
    private Date checkIn;
    private Date checkOut;
    private Long customerId;
    private String status;
    private List<ReservationRoomDTO> rooms;
    private List<ServiceOrderDTO> services;

    public ReservationCreateDTO() {
    }

    public ReservationCreateDTO(Date checkIn, Date checkOut, Long customerId, String status, 
                               List<ReservationRoomDTO> rooms, List<ServiceOrderDTO> services) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.customerId = customerId;
        this.status = status;
        this.rooms = rooms;
        this.services = services;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ReservationRoomDTO> getRooms() {
        return rooms;
    }

    public void setRooms(List<ReservationRoomDTO> rooms) {
        this.rooms = rooms;
    }

    public List<ServiceOrderDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceOrderDTO> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "ReservationCreateDTO{" +
                "checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", customerId=" + customerId +
                ", status='" + status + '\'' +
                ", rooms=" + rooms +
                ", services=" + services +
                '}';
    }
}
