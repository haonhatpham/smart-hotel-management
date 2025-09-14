package com.pnh.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for ReservationRoom data transfer
 */
public class ReservationRoomDTO {
    private Long roomId;
    private Date checkIn;
    private Date checkOut;
    private BigDecimal pricePerNight;
    private String notes;

    public ReservationRoomDTO() {
    }

    public ReservationRoomDTO(Long roomId, Date checkIn, Date checkOut, BigDecimal pricePerNight, String notes) {
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.pricePerNight = pricePerNight;
        this.notes = notes;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "ReservationRoomDTO{" +
                "roomId=" + roomId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", pricePerNight=" + pricePerNight +
                ", notes='" + notes + '\'' +
                '}';
    }
}
