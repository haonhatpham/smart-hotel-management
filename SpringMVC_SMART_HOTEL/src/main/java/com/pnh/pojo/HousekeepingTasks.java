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
@Table(name = "housekeeping_tasks")
@NamedQueries({
    @NamedQuery(name = "HousekeepingTasks.findAll", query = "SELECT h FROM HousekeepingTasks h"),
    @NamedQuery(name = "HousekeepingTasks.findById", query = "SELECT h FROM HousekeepingTasks h WHERE h.id = :id"),
    @NamedQuery(name = "HousekeepingTasks.findByTask", query = "SELECT h FROM HousekeepingTasks h WHERE h.task = :task"),
    @NamedQuery(name = "HousekeepingTasks.findByStatus", query = "SELECT h FROM HousekeepingTasks h WHERE h.status = :status"),
    @NamedQuery(name = "HousekeepingTasks.findByDueTime", query = "SELECT h FROM HousekeepingTasks h WHERE h.dueTime = :dueTime"),
    @NamedQuery(name = "HousekeepingTasks.findByCreatedAt", query = "SELECT h FROM HousekeepingTasks h WHERE h.createdAt = :createdAt"),
    @NamedQuery(name = "HousekeepingTasks.findByUpdatedAt", query = "SELECT h FROM HousekeepingTasks h WHERE h.updatedAt = :updatedAt")})
public class HousekeepingTasks implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "task")
    private String task;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 11)
    @Column(name = "status")
    private String status;
    @Column(name = "due_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueTime;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Rooms roomId;
    @JoinColumn(name = "assignee_id", referencedColumnName = "id")
    @ManyToOne
    private Users assigneeId;

    public HousekeepingTasks() {
    }

    public HousekeepingTasks(Long id) {
        this.id = id;
    }

    public HousekeepingTasks(Long id, String task, String status, Date createdAt) {
        this.id = id;
        this.task = task;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Rooms getRoomId() {
        return roomId;
    }

    public void setRoomId(Rooms roomId) {
        this.roomId = roomId;
    }

    public Users getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Users assigneeId) {
        this.assigneeId = assigneeId;
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
        if (!(object instanceof HousekeepingTasks)) {
            return false;
        }
        HousekeepingTasks other = (HousekeepingTasks) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pnh.pojo.HousekeepingTasks[ id=" + id + " ]";
    }
    
}
