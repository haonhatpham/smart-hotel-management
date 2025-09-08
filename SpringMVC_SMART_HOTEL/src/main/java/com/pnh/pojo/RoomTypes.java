/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "room_types")
@NamedQueries({
    @NamedQuery(name = "RoomTypes.findAll", query = "SELECT r FROM RoomTypes r"),
    @NamedQuery(name = "RoomTypes.findById", query = "SELECT r FROM RoomTypes r WHERE r.id = :id"),
    @NamedQuery(name = "RoomTypes.findByName", query = "SELECT r FROM RoomTypes r WHERE r.name = :name"),
    @NamedQuery(name = "RoomTypes.findByPrice", query = "SELECT r FROM RoomTypes r WHERE r.price = :price"),
    @NamedQuery(name = "RoomTypes.findByCapacity", query = "SELECT r FROM RoomTypes r WHERE r.capacity = :capacity"),
    @NamedQuery(name = "RoomTypes.findByActive", query = "SELECT r FROM RoomTypes r WHERE r.active = :active")})
public class RoomTypes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "name")
    private String name;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "capacity")
    private int capacity;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roomTypeId")
    private Set<Rooms> roomsSet;

    public RoomTypes() {
    }

    public RoomTypes(Long id) {
        this.id = id;
    }

    public RoomTypes(Long id, String name, BigDecimal price, int capacity, boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.capacity = capacity;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Rooms> getRoomsSet() {
        return roomsSet;
    }

    public void setRoomsSet(Set<Rooms> roomsSet) {
        this.roomsSet = roomsSet;
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
        if (!(object instanceof RoomTypes)) {
            return false;
        }
        RoomTypes other = (RoomTypes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pnh.pojo.RoomTypes[ id=" + id + " ]";
    }
    
}
