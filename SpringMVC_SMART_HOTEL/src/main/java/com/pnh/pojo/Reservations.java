/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
@Entity
@JsonIgnoreProperties({
    "reservationRoomsSet",
    "paymentsSet",
    "serviceOrdersSet",
    "reviewsSet",
    "invoices",
    "createdBy"
})
@Table(name = "reservations")
@NamedQueries({
    @NamedQuery(name = "Reservations.findAll", query = "SELECT r FROM Reservations r"),
    @NamedQuery(name = "Reservations.findById", query = "SELECT r FROM Reservations r WHERE r.id = :id"),
    @NamedQuery(name = "Reservations.findByCheckIn", query = "SELECT r FROM Reservations r WHERE r.checkIn = :checkIn"),
    @NamedQuery(name = "Reservations.findByCheckOut", query = "SELECT r FROM Reservations r WHERE r.checkOut = :checkOut"),
    @NamedQuery(name = "Reservations.findByStatus", query = "SELECT r FROM Reservations r WHERE r.status = :status"),
    @NamedQuery(name = "Reservations.findByCreatedAt", query = "SELECT r FROM Reservations r WHERE r.createdAt = :createdAt"),
    @NamedQuery(name = "Reservations.findByUpdatedAt", query = "SELECT r FROM Reservations r WHERE r.updatedAt = :updatedAt")})
public class Reservations implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "check_in")
    @Temporal(TemporalType.DATE)
    private Date checkIn;
    @Basic(optional = false)
    @NotNull
    @Column(name = "check_out")
    @Temporal(TemporalType.DATE)
    private Date checkOut;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 11)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reservationId")
    private Set<ReservationRooms> reservationRoomsSet;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "reservationId")
    private Invoices invoices;
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    @ManyToOne
    private Users createdBy;
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Users customerId;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reservationId")
    private Set<Reviews> reviewsSet;
     @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reservationId")
    private Set<Payments> paymentsSet;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reservationId")
    private Set<ServiceOrders> serviceOrdersSet;

    public Reservations() {
    }

    public Reservations(Long id) {
        this.id = id;
    }

    public Reservations(Long id, Date checkIn, Date checkOut, String status, Date createdAt) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ReservationRooms> getReservationRoomsSet() {
        return reservationRoomsSet;
    }

    public void setReservationRoomsSet(Set<ReservationRooms> reservationRoomsSet) {
        this.reservationRoomsSet = reservationRoomsSet;
    }

    public Invoices getInvoices() {
        return invoices;
    }

    public void setInvoices(Invoices invoices) {
        this.invoices = invoices;
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public Users getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Users customerId) {
        this.customerId = customerId;
    }

    public Set<Reviews> getReviewsSet() {
        return reviewsSet;
    }

    public void setReviewsSet(Set<Reviews> reviewsSet) {
        this.reviewsSet = reviewsSet;
    }

    public Set<Payments> getPaymentsSet() {
        return paymentsSet;
    }

    public void setPaymentsSet(Set<Payments> paymentsSet) {
        this.paymentsSet = paymentsSet;
    }

    public Set<ServiceOrders> getServiceOrdersSet() {
        return serviceOrdersSet;
    }

    public void setServiceOrdersSet(Set<ServiceOrders> serviceOrdersSet) {
        this.serviceOrdersSet = serviceOrdersSet;
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
        if (!(object instanceof Reservations)) {
            return false;
        }
        Reservations other = (Reservations) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pnh.pojo.Reservations[ id=" + id + " ]";
    }
    
 
    
}
