/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pnh.repositories;
import com.pnh.pojo.Payments;
import java.util.List;
/**
 *
 * @author User
 */
public interface PaymentRepository {
    Payments createPayment(Payments payment);
    Payments updatePayment(Payments payment);
    Payments getPaymentById(int id);
    List<Payments> getPaymentsByEnrollmentId(int enrollmentId);
    List<Payments> getPaymentsByStatus(String status);
    Payments getPaymentByTransactionId(String transactionId);    
}
