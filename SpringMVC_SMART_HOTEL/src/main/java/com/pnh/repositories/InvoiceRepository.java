/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pnh.repositories;

import com.pnh.pojo.Invoices;
import java.util.Optional;

public interface InvoiceRepository {
    Invoices save(Invoices invoice);
    Optional<Invoices> findById(Long id);
    boolean existsByReservationId(Long reservationId);
    
    Optional<Invoices> findByIdWithDetails(Long id);
}