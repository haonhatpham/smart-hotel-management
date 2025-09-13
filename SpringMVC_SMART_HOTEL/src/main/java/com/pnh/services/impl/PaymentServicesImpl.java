/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnh.pojo.Payments;
import com.pnh.repositories.PaymentRepository;
import com.pnh.services.PaymentService;
import static com.pnh.utils.HmacUtil.hmacSHA256;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static com.pnh.utils.HmacUtil.hmacSHA512;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author User
 */
@Service
@Transactional
@PropertySource("classpath:payment-config.properties")
public class PaymentServicesImpl implements PaymentService {

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
            payment.setStatus("PENDING");

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
        String orderId = generateTransactionId(); // luôn unique
        String requestId = generateTransactionId(); // cũng unique
        try {
            // 1. Chuẩn bị params
            Map<String, String> params = new LinkedHashMap<>();
            params.put("partnerCode", momoPartnerCode);
            params.put("requestId", requestId);
            params.put("amount", String.valueOf(amount != null ? Math.round(amount) : 0));
            params.put("orderId", orderId);
            params.put("orderInfo", "Payment for reservation");
            params.put("redirectUrl", momoRedirectUrl);
            params.put("ipnUrl", momoIpnUrl);
            params.put("requestType", "payWithMethod");
            params.put("extraData",  String.valueOf(paymentId));

            // 2. Tạo raw signature
            String rawSignature = "accessKey=" + momoAccessKey
                    + "&amount=" + params.get("amount")
                    + "&extraData=" + params.get("extraData")
                    + "&ipnUrl=" + params.get("ipnUrl")
                    + "&orderId=" + params.get("orderId")
                    + "&orderInfo=" + params.get("orderInfo")
                    + "&partnerCode=" + params.get("partnerCode")
                    + "&redirectUrl=" + params.get("redirectUrl")
                    + "&requestId=" + params.get("requestId")
                    + "&requestType=" + params.get("requestType");

            String signature = hmacSHA256(rawSignature, momoSecretKey);
            params.put("signature", signature);

            // 3. Call API bằng RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonRequest = new ObjectMapper().writeValueAsString(params);
            System.out.println("=== MoMo Request JSON ===");
            System.out.println(jsonRequest);

            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(momoEndpoint, entity, String.class);

            System.out.println("=== MoMo Response ===");
            System.out.println(response.getBody());

            // 4. Parse JSON response
            JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

            if (jsonNode.has("resultCode") && jsonNode.get("resultCode").asInt() != 0) {
                String msg = jsonNode.has("message") ? jsonNode.get("message").asText() : "Unknown error";
                throw new RuntimeException("MoMo payment error: " + msg);
            }

            if (!jsonNode.has("payUrl")) {
                throw new RuntimeException("MoMo payment error: Missing payUrl in response");
            }

            return jsonNode.get("payUrl").asText();

        } catch (Exception e) {
            throw new RuntimeException("MoMo payment error: " + e.getMessage(), e);
        }
    }

    @Override
    public String createVNPayPaymentUrl(Long paymentId, Double amount) {
        // ===== params =====
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayTmnCode);

        long vnpAmount = amount != null ? Math.round(amount * 100) : 0L;
        vnp_Params.put("vnp_Amount", String.valueOf(vnpAmount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(paymentId));
        vnp_Params.put("vnp_OrderInfo", "Payment for reservation");
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_ReturnUrl", vnpayReturnUrl);
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // ===== build HashData (sorted) and Query =====
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                try {
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString());
                    hashData.append(fieldName).append("=").append(encodedValue);
                    query.append(fieldName).append("=").append(encodedValue);
                    if (itr.hasNext()) {
                        hashData.append("&");
                        query.append("&");
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(PaymentServicesImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        String vnp_SecureHash = "";
        try {
            vnp_SecureHash = hmacSHA512(hashData.toString(), vnpayHashSecret);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnpayUrl + "?" + query.toString();
    }

    @Override
    public Payments updatePayment(Payments payment) {
        return paymentRepository.updatePayment(payment);
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
