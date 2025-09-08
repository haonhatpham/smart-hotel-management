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
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "customer_profiles")
@NamedQueries({
    @NamedQuery(name = "CustomerProfiles.findAll", query = "SELECT c FROM CustomerProfiles c"),
    @NamedQuery(name = "CustomerProfiles.findById", query = "SELECT c FROM CustomerProfiles c WHERE c.id = :id"),
    @NamedQuery(name = "CustomerProfiles.findByDob", query = "SELECT c FROM CustomerProfiles c WHERE c.dob = :dob"),
    @NamedQuery(name = "CustomerProfiles.findByAddress", query = "SELECT c FROM CustomerProfiles c WHERE c.address = :address"),
    @NamedQuery(name = "CustomerProfiles.findByLoyaltyPoint", query = "SELECT c FROM CustomerProfiles c WHERE c.loyaltyPoint = :loyaltyPoint")})
public class CustomerProfiles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private Date dob;
    @Size(max = 500)
    @Column(name = "address")
    private String address;
    @Basic(optional = false)
    @NotNull
    @Column(name = "loyalty_point")
    private int loyaltyPoint;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OneToOne(optional = false)
    private Users userId;

    public CustomerProfiles() {
    }

    public CustomerProfiles(Long id) {
        this.id = id;
    }

    public CustomerProfiles(Long id, int loyaltyPoint) {
        this.id = id;
        this.loyaltyPoint = loyaltyPoint;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLoyaltyPoint() {
        return loyaltyPoint;
    }

    public void setLoyaltyPoint(int loyaltyPoint) {
        this.loyaltyPoint = loyaltyPoint;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
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
        if (!(object instanceof CustomerProfiles)) {
            return false;
        }
        CustomerProfiles other = (CustomerProfiles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pnh.pojo.CustomerProfiles[ id=" + id + " ]";
    }
    
}
