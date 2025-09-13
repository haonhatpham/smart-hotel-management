/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.controllers;

import com.pnh.pojo.Payments;
import com.pnh.pojo.Reservations;
import com.pnh.services.PaymentService;
import com.pnh.services.ReservationService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static com.pnh.utils.HmacUtil.hmacSHA512;
import java.math.BigDecimal;

/**
 *
 * @author User
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiPaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationService reservationService;

    @Value("${vnpay.hashSecret}")
    private String vnpayHashSecret;
    @Value("${frontend.baseUrl}")
    private String frontendBaseUrl;

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> paymentRequest) {
        try {
            Number reservationIdNum = (Number) paymentRequest.get("reservationId");
            Number amountNum = (Number) paymentRequest.get("amount");
            String paymentMethod = (String) paymentRequest.get("paymentMethod");

            if (reservationIdNum == null || amountNum == null || paymentMethod == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thiếu tham số: reservationId, amount, paymentMethod"
                ));
            }

            Long reservationId = reservationIdNum.longValue();
            Double amount = amountNum.doubleValue();

            Reservations reservation = reservationService.getById(reservationId);
            if (reservation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "Đặt phòng không tồn tại"
                ));
            }

            Payments payment = new Payments();
            payment.setReservationId(reservation);
            payment.setAmount(java.math.BigDecimal.valueOf(amount));
            payment.setMethod(paymentMethod);
            payment.setCreatedAt(new Date());
            payment.setStatus("PENDING");

            Payments savedPayment = paymentService.createPayment(payment);

            String paymentUrl;
            if ("MOMO".equalsIgnoreCase(paymentMethod)) {
                paymentUrl = paymentService.createMoMoPaymentUrl(savedPayment.getId(), amount);
            } else if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
                paymentUrl = paymentService.createVNPayPaymentUrl(savedPayment.getId(), amount);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Phương thức thanh toán không hợp lệ"
                ));
            }

            paymentService.updatePaymentStatus(savedPayment.getId(), "PENDING");

            String successUrl = frontendBaseUrl + "/thankyou/result?success=true&method=" + paymentMethod
                    + "&orderId=" + savedPayment.getId() + (amount != null ? "&amount=" + amount : "");

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Tạo thanh toán thành công",
                    "paymentUrl", paymentUrl,
                    "paymentId", savedPayment.getId(),
                    "successUrl", successUrl
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Có lỗi xảy ra: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/status/{transactionId}")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable String transactionId) {
        try {
            boolean isSuccess = paymentService.verifyPayment(transactionId);

            return ResponseEntity.ok(Map.of(
                    "success", isSuccess,
                    "transactionId", transactionId,
                    "message", isSuccess ? "Thanh toán đã được xác nhận"
                            : "Thanh toán chưa hoàn tất hoặc không tồn tại"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi kiểm tra trạng thái: " + e.getMessage()
            ));
        }
    }

    @RequestMapping(value = "/callback/momo")
    public ResponseEntity<?> momoCallback(@RequestBody Map<String, String> callbackData) {
    try {
        String orderId = callbackData.get("orderId");
        String resultCode = callbackData.get("resultCode");
        String transId = callbackData.get("transId");
        String extraData = callbackData.get("extraData"); // nếu bạn dùng extraData để lưu paymentId

        if (extraData == null) {
            return ResponseEntity.badRequest().body("Missing extraData parameter");
        }

        Long paymentId = Long.parseLong(extraData);
        Payments payment = paymentService.getPaymentById(paymentId);

        if (payment != null) {
            payment.setTransactionId(transId);
            if ("0".equals(resultCode)) {
                payment.setStatus("SUCCESS");
                payment.setPaidAt(new Date());
                Reservations r = payment.getReservationId();
                if (r != null) {
                    reservationService.updateStatus(r.getId(), "CONFIRMED");
                }
            } else {
                payment.setStatus("FAILED");
            }
            paymentService.updatePayment(payment);
        }

        return ResponseEntity.ok("IPN received");

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
    }
}

    @GetMapping("/callback/momo/guest")
    public ResponseEntity<?> momoReturn(@RequestParam Map<String, String> params) {
        String orderId = params.get("orderId");
        String resultCode = params.get("resultCode");
        boolean success = "0".equals(resultCode);
        try {
            Long paymentId = Long.parseLong(orderId);
            Payments payment = paymentService.getPaymentById(paymentId);
            Long reservationId = payment != null && payment.getReservationId() != null ? payment.getReservationId().getId() : null;
            BigDecimal amount = payment != null ? payment.getAmount() : null;
            String redirectUrl = frontendBaseUrl + "/thankyou/result"
                    + "?success=" + success
                    + "&method=MOMO"
                    + "&orderId=" + paymentId
                    + (amount != null ? "&amount=" + amount : "");
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
        } catch (Exception e) {
            String fallback = frontendBaseUrl + "/thankyou/result?success=" + success + "&method=MOMO&orderId=" + orderId;
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", fallback).build();
        }
    }

    @RequestMapping(value = "/callback/vnpay", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> vnpayCallback(@RequestParam Map<String, String> callbackData) {
        try {
            if (!verifyVNPaySignature(callbackData)) {
                return ResponseEntity.badRequest().body("INVALID_SIGNATURE");
            }

            String vnp_TxnRef = callbackData.get("vnp_TxnRef");
            String vnp_ResponseCode = callbackData.get("vnp_ResponseCode");
            Long paymentId = Long.parseLong(vnp_TxnRef);

            Payments payment = paymentService.getPaymentById(paymentId);
            if (payment != null) {
                String vnpTransNo = callbackData.get("vnp_TransactionNo");
                if (vnpTransNo != null && !vnpTransNo.isEmpty()) {
                    payment.setTransactionId(vnpTransNo);
                }
                if ("00".equals(vnp_ResponseCode)) {
                    payment.setStatus("SUCCESS");
                    payment.setPaidAt(new Date());
                    Reservations r = payment.getReservationId();
                    if (r != null) {
                        reservationService.updateStatus(r.getId(), "CONFIRMED");
                    }
                } else {
                    payment.setStatus("FAILED");
                }
                paymentService.updatePayment(payment);
            }

            boolean success = "00".equals(vnp_ResponseCode);
            Long reservationId = payment != null && payment.getReservationId() != null ? payment.getReservationId().getId() : null;
            BigDecimal amount = payment != null ? payment.getAmount() : null;
            String paymentMethod = "VNPAY";

            String redirectUrl = frontendBaseUrl + "/thankyou/result"
                    + "?success=" + success
                    + "&method=" + paymentMethod
                    + (reservationId != null ? "&orderId=" + reservationId : "")
                    + (amount != null ? "&amount=" + amount.longValue() : "");

            return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
        }
    }

    private boolean verifyVNPaySignature(Map<String, String> callbackData) {
        try {
            String vnp_SecureHash = callbackData.get("vnp_SecureHash");
            if (vnp_SecureHash == null) {
                return false;
            }

            Map<String, String> fields = new HashMap<>(callbackData);
            fields.remove("vnp_SecureHash");
            fields.remove("vnp_SecureHashType");

            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fieldNames.size(); i++) {
                String key = fieldNames.get(i);
                String value = fields.get(key);
                if (value != null && !value.isEmpty()) {
                    sb.append(key).append("=")
                            .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                    if (i < fieldNames.size() - 1) {
                        sb.append("&");
                    }
                }
            }

            String signValue = hmacSHA512(sb.toString(), vnpayHashSecret);
            return signValue.equalsIgnoreCase(vnp_SecureHash);

        } catch (Exception e) {
            return false;
        }
    }

}
