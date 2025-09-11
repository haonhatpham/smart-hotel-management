/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.pojo.Payments;
import com.pnh.repositories.PaymentRepository;
import com.pnh.services.PaymentService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author User
 */
@Service
@Transactional
@PropertySource("classpath:payment-config.properties")
public class PaymentServicesImpl implements PaymentService{
    @Value("${momo.partnerCode}")
    private String momoPartnerCode;

    @Value("${momo.endpoint}")
    private String momoEndpoint;
    @Value("${momo.accessKey}")
    private String momoAccessKey;

    @Value("${momo.secretKey}")
    private String momoSecretKey;

    @Value("${momo.redirectUrl}")
    private String momoRedirectUrl;

    @Value("${momo.ipnUrl}")
    private String momoIpnUrl;

    @Value("${vnpay.tmnCode}")
    private String vnpayTmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnpayHashSecret;

    @Value("${vnpay.url}")
    private String vnpayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnpayReturnUrl;
    
    
    @Autowired
    private PaymentRepository paymentRepository;
    @Override
    @Transactional
    public Payments processPayment(Payments payment) {
        try {
            payment.setTransactionId(generateTransactionId());
            payment.setCreatedAt(new Date());
            payment.setStatus("PROCESSING");

            Payments savedPayment = paymentRepository.createPayment(payment);

            if ("MOMO".equals(payment.getMethod())) {
                String momoUrl = createMoMoPaymentUrl(savedPayment.getId(), savedPayment.getAmount() != null ? savedPayment.getAmount().doubleValue() : 0D);
                savedPayment.setStatus("PENDING");
                savedPayment = paymentRepository.updatePayment(savedPayment);

            } else if ("VNPAY".equals(payment.getMethod())) {
                String vnpayUrl = createVNPayPaymentUrl(savedPayment.getId(), savedPayment.getAmount() != null ? savedPayment.getAmount().doubleValue() : 0D);
                savedPayment.setStatus("PENDING");
                savedPayment = paymentRepository.updatePayment(savedPayment);
            }

            return savedPayment;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý thanh toán: " + e.getMessage());
        }
    }

    @Override
    public Payments createPayment(Payments payment) {
        payment.setCreatedAt(new Date());
        payment.setStatus("PENDING");
        return paymentRepository.createPayment(payment);
    }

    @Override
    public Payments updatePaymentStatus(Long paymentId, String status) {
        Payments payment = paymentRepository.getPaymentById(paymentId);
        if (payment != null) {
            payment.setStatus(status);
            return paymentRepository.updatePayment(payment);
        }
        return null;
    }

    @Override
    public Payments getPaymentById(Long id) {
        return paymentRepository.getPaymentById(id);
    }

    @Override
    public List<Payments> getPaymentsByReservationId(Long reservationId) {
        return paymentRepository.getPaymentsByReservationId(reservationId);
    }

    @Override
    public Payments getPaymentByTransactionId(String transactionId) {
        return paymentRepository.getPaymentByTransactionId(transactionId);
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        Payments payment = paymentRepository.getPaymentByTransactionId(transactionId);
        return payment != null && "SUCCESS".equalsIgnoreCase(payment.getStatus());
    }

    @Override
    public String createMoMoPaymentUrl(Long paymentId, Double amount) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("partnerCode", momoPartnerCode);
        params.put("requestId", String.valueOf(paymentId));
        params.put("amount", String.valueOf(amount != null ? Math.round(amount) : 0));
        params.put("orderId", String.valueOf(paymentId));
        params.put("orderInfo", "Payment for reservation");
        params.put("redirectUrl", momoRedirectUrl);
        params.put("ipnUrl", momoIpnUrl);
        params.put("requestType", "payWithMethod");

        StringBuilder query = new StringBuilder(momoEndpoint).append("?");
        boolean first = true;
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (!first) query.append("&");
            query.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                 .append("=")
                 .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
            first = false;
        }
        return query.toString();
    }

    @Override
    public String createVNPayPaymentUrl(Long paymentId, Double amount) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_TmnCode", vnpayTmnCode);
        params.put("vnp_TxnRef", String.valueOf(paymentId));
        long vnpAmount = amount != null ? Math.round(amount * 100) : 0L;
        params.put("vnp_Amount", String.valueOf(vnpAmount));
        params.put("vnp_OrderInfo", "Payment for reservation");
        params.put("vnp_ReturnUrl", vnpayReturnUrl);
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");

        StringBuilder query = new StringBuilder(vnpayUrl).append("?");
        boolean first = true;
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (!first) query.append("&");
            query.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                 .append("=")
                 .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
            first = false;
        }
        return query.toString();
    }

    @Override
    public Payments updatePayment(Payments payment) {
        return paymentRepository.updatePayment(payment);
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
