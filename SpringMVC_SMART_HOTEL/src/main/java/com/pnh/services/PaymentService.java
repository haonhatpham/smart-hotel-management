/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pnh.services;
import com.pnh.pojo.Payments;
/**
 *
 * @author User
 */
import java.util.List;
public interface PaymentService {
    Payments processPayment(Payments payment);
    Payments createPayment(Payments payment);
    Payments updatePaymentStatus(Long paymentId, String status);
    Payments getPaymentById(Long id);
    Payments getPaymentByTransactionId(String transactionId);
    List<Payments> getPaymentsByReservationId(Long reservationId);
    boolean verifyPayment(String transactionId);
    String createMoMoPaymentUrl(Long paymentId,  Double amount);
    String createVNPayPaymentUrl(Long paymentId, Double amount);
    Payments updatePayment(Payments payment);
}
