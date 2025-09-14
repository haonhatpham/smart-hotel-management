package com.pnh.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for ServiceOrder data transfer
 */
public class ServiceOrderDTO {
    private Long serviceId;
    private int qty;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private Date orderedAt;
    private String notes;

    public ServiceOrderDTO() {
    }

    public ServiceOrderDTO(Long serviceId, int qty, BigDecimal unitPrice, BigDecimal amount, Date orderedAt, String notes) {
        this.serviceId = serviceId;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.orderedAt = orderedAt;
        this.notes = notes;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(Date orderedAt) {
        this.orderedAt = orderedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "ServiceOrderDTO{" +
                "serviceId=" + serviceId +
                ", qty=" + qty +
                ", unitPrice=" + unitPrice +
                ", amount=" + amount +
                ", orderedAt=" + orderedAt +
                ", notes='" + notes + '\'' +
                '}';
    }
}
