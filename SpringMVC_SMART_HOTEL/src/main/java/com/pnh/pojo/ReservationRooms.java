/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "reservation_rooms")
@NamedQueries({
    @NamedQuery(name = "ReservationRooms.findAll", query = "SELECT r FROM ReservationRooms r"),
    @NamedQuery(name = "ReservationRooms.findById", query = "SELECT r FROM ReservationRooms r WHERE r.id = :id"),
    @NamedQuery(name = "ReservationRooms.findByPricePerNight", query = "SELECT r FROM ReservationRooms r WHERE r.pricePerNight = :pricePerNight")})
public class ReservationRooms implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price_per_night")
    private BigDecimal pricePerNight;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Reservations reservationId;
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Rooms roomId;

    public ReservationRooms() {
    }

    public ReservationRooms(Long id) {
        this.id = id;
    }

    public ReservationRooms(Long id, BigDecimal pricePerNight) {
        this.id = id;
        this.pricePerNight = pricePerNight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Reservations getReservationId() {
        return reservationId;
    }

    public void setReservationId(Reservations reservationId) {
        this.reservationId = reservationId;
    }

    public Rooms getRoomId() {
        return roomId;
    }

    public void setRoomId(Rooms roomId) {
        this.roomId = roomId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReservationRooms)) {
            return false;
        }
        ReservationRooms other = (ReservationRooms) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pnh.pojo.ReservationRooms[ id=" + id + " ]";
    }
    
}
