/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.pojo.Invoices;
import com.pnh.repositories.InvoiceRepository;
import com.pnh.services.InvoiceService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author User
 */
@Service
@Transactional
public class InvoiceServicesImpl implements InvoiceService{
    
    @Autowired 
    private InvoiceRepository invoicerepository;
    
    @Override
    public Invoices save(Invoices invoice) {
        return invoicerepository.save(invoice);
    }

    @Override
    public Optional<Invoices> findById(Long id) {
        return invoicerepository.findById(id);
    }

    @Override
    public boolean existsByReservationId(Long reservationId) {
        return invoicerepository.existsByReservationId(reservationId);
    }

    @Override
    public Optional<Invoices> findByIdWithDetails(Long id) {
        return invoicerepository.findByIdWithDetails(id);
    }
    
}
