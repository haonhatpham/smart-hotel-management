/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.controllers;

import com.pnh.pojo.Invoices;
import com.pnh.pojo.Payments;
import com.pnh.pojo.Reservations;
import com.pnh.pojo.Users;
import com.pnh.services.InvoiceService;
import com.pnh.services.LoyaltyService;
import com.pnh.services.PaymentService;
import com.pnh.services.ReservationService;
import com.pnh.services.UserService;
import java.net.URLEncoder;
import java.security.Principal;
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
import com.pnh.utils.MailUtil;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiPaymentController {

    private static final Logger LOG = Logger.getLogger(ApiPaymentController.class.getName());

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private InvoiceService invoiceSerive;

    @Autowired
    private LoyaltyService loyaltyService;

    @Autowired
    private UserService userService;

    @Value("${vnpay.hashSecret}")
    private String vnpayHashSecret;
    @Value("${frontend.baseUrl}")
    private String frontendBaseUrl;

    private boolean canAccessReservation(Reservations r, Users currentUser) {
        if (currentUser == null || r == null || r.getCustomerId() == null) return false;
        if (r.getCustomerId().getId().equals(currentUser.getId())) return true;
        String role = currentUser.getRole() != null ? currentUser.getRole() : "";
        return "ADMIN".equals(role) || "RECEPTION".equals(role);
    }

    @PostMapping("/secure/process")
    public ResponseEntity<?> processPayment(Principal principal, @RequestBody Map<String, Object> paymentRequest) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "Chưa đăng nhập"
                ));
            }
            Users currentUser = userService.getUserByUsername(principal.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "Không tìm thấy người dùng"
                ));
            }

            Number reservationIdNum = (Number) paymentRequest.get("reservationId");
            Number amountNum = (Number) paymentRequest.get("amount");
            String paymentMethod = (String) paymentRequest.get("paymentMethod");
            Object usePointsRaw = paymentRequest.get("usePoints");
            boolean usePoints = false;
            if (usePointsRaw instanceof Boolean) {
                usePoints = (Boolean) usePointsRaw;
            } else if (usePointsRaw instanceof String) {
                usePoints = Boolean.parseBoolean((String) usePointsRaw);
            }

            if (reservationIdNum == null || amountNum == null || paymentMethod == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thiếu tham số: reservationId, amount, paymentMethod"
                ));
            }

            Long reservationId = reservationIdNum.longValue();
            Double amount = amountNum.doubleValue();
            BigDecimal originalAmount = BigDecimal.valueOf(amount);

            Reservations reservation = reservationService.getById(reservationId);
            if (reservation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "Đặt phòng không tồn tại"
                ));
            }
            if (!canAccessReservation(reservation, currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "success", false,
                        "message", "Bạn không có quyền thanh toán đơn này"
                ));
            }

            // Áp dụng đổi điểm nếu người dùng chọn sử dụng loyalty
            BigDecimal finalAmount = originalAmount;
            Long customerId = reservation.getCustomerId() != null ? reservation.getCustomerId().getId() : null;
            if (usePoints && customerId != null) {
                try {
                    finalAmount = loyaltyService.applyRedemption(customerId, reservationId, originalAmount);
                } catch (Exception ex) {
                    LOG.log(java.util.logging.Level.WARNING, "Loyalty redemption failed", ex);
                    finalAmount = originalAmount;
                }
            }

            Payments payment = new Payments();
            payment.setReservationId(reservation);
            payment.setAmount(finalAmount);
            payment.setMethod(paymentMethod);
            payment.setCreatedAt(new Date());
            payment.setStatus("PENDING");

            Payments savedPayment = paymentService.createPayment(payment);

            String paymentUrl;
            if ("WALLET".equalsIgnoreCase(paymentMethod)) {
                paymentUrl = paymentService.createMoMoPaymentUrl(savedPayment.getId(), finalAmount.doubleValue());
                paymentService.updatePaymentStatus(savedPayment.getId(), "PENDING");
            } else if ("CARD".equalsIgnoreCase(paymentMethod)) {
                paymentUrl = paymentService.createVNPayPaymentUrl(savedPayment.getId(), finalAmount.doubleValue());
                paymentService.updatePaymentStatus(savedPayment.getId(), "PENDING");
            } else if ("CASH".equalsIgnoreCase(paymentMethod)) {
                // Thanh toán tại quầy: đánh dấu là SUCCESS và cập nhật reservation
                paymentService.updatePaymentStatus(savedPayment.getId(), "SUCCESS");
                payment.setPaidAt(new Date());
                paymentService.updatePayment(payment);
                reservationService.updateStatus(reservation.getId(), "CONFIRMED");
                Invoices invoice = new Invoices();
                invoice.setReservationId(reservation);
                invoice.setIssuedAt(new Date());
                invoice.setTotalAmount(payment.getAmount());

                invoiceSerive.save(invoice);

                try {
                    Long cashCustomerId = reservation.getCustomerId() != null ? reservation.getCustomerId().getId() : null;
                    if (cashCustomerId != null && payment.getAmount() != null) {
                        loyaltyService.addPoints(cashCustomerId, reservation.getId(), payment.getAmount());
                    }
                } catch (Exception e) {
                    LOG.log(java.util.logging.Level.WARNING, "Loyalty addPoints failed", e);
                }

                try {
                    String email = reservation.getCustomerId() != null ? reservation.getCustomerId().getEmail() : null;
                    if (email != null) {
                        String[] subjBody = buildBookingConfirmationEmail(reservation, payment.getAmount());
                        MailUtil.sendMailAsync(email, subjBody[0], subjBody[1]);
                    }
                } catch (Exception mailEx) {
                    LOG.log(java.util.logging.Level.WARNING, "Send booking email failed", mailEx);
                }

                String successUrl = frontendBaseUrl + "/thankyou/result?success=true&method=" + paymentMethod
                        + "&orderId=" + savedPayment.getId() + (finalAmount != null ? "&amount=" + finalAmount : "");

                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                        "success", true,
                        "message", "Đặt phòng thành công! Vui lòng thanh toán tại quầy khi nhận phòng.",
                        "paymentId", savedPayment.getId(),
                        "successUrl", successUrl
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Phương thức thanh toán không hợp lệ"
                ));
            }

            String successUrl = frontendBaseUrl + "/thankyou/result?success=false&method=" + paymentMethod
                    + "&orderId=" + savedPayment.getId() + (amount != null ? "&amount=" + amount : "");

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Tạo thanh toán thành công",
                    "paymentUrl", paymentUrl,
                    "paymentId", savedPayment.getId(),
                    "successUrl", successUrl
            ));

        } catch (Exception e) {
            LOG.log(java.util.logging.Level.SEVERE, "processPayment error", e);
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
                        Invoices invoice = new Invoices();
                        invoice.setReservationId(r);
                        invoice.setIssuedAt(new Date());
                        invoice.setTotalAmount(payment.getAmount());
                        invoiceSerive.save(invoice);
                        try {
                            Long customerId = r.getCustomerId() != null ? r.getCustomerId().getId() : null;
                            if (customerId != null && payment.getAmount() != null) {
                                loyaltyService.addPoints(customerId, r.getId(), payment.getAmount());
                            }
                        } catch (Exception e) {
                            LOG.log(java.util.logging.Level.WARNING, "Loyalty addPoints in IPN failed", e);
                        }
                        try {
                            String email = r.getCustomerId().getEmail();
                            if (email != null) {
                                String[] subjBody = buildBookingConfirmationEmail(r, payment.getAmount());
                                MailUtil.sendMailAsync(email, subjBody[0], subjBody[1]);
                            }
                        } catch (Exception mailEx) {
                            LOG.log(java.util.logging.Level.WARNING, "Send mail in IPN failed", mailEx);
                        }
                    }
                } else {
                    payment.setStatus("FAILED");
                }
                paymentService.updatePayment(payment);
            }

            return ResponseEntity.ok("IPN received");

        } catch (Exception e) {
            LOG.log(java.util.logging.Level.SEVERE, "MoMo IPN error", e);
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

                    Invoices invoice = new Invoices();
                    invoice.setReservationId(r);
                    invoice.setIssuedAt(new Date());
                    invoice.setTotalAmount(payment.getAmount());

                    invoiceSerive.save(invoice);
                    if (r != null) {
                        reservationService.updateStatus(r.getId(), "CONFIRMED");
                        try {
                            Long customerId = r.getCustomerId() != null ? r.getCustomerId().getId() : null;
                            if (customerId != null && payment.getAmount() != null) {
                                loyaltyService.addPoints(customerId, r.getId(), payment.getAmount());
                            }
                        } catch (Exception e) {
                            LOG.log(java.util.logging.Level.WARNING, "Loyalty addPoints in VNPay callback failed", e);
                        }
                        try {
                            String email = r.getCustomerId().getEmail();
                            if (email != null) {
                                String[] subjBody = buildBookingConfirmationEmail(r, payment.getAmount());
                                MailUtil.sendMailAsync(email, subjBody[0], subjBody[1]);
                            }
                        } catch (Exception mailEx) {
                            LOG.log(java.util.logging.Level.WARNING, "Send mail in VNPay callback failed", mailEx);
                        }

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

    /** Xác nhận đặt phòng + nhắc check-in + link chi tiết & đánh giá (HTML đẹp) */
    private String[] buildBookingConfirmationEmail(Reservations r, BigDecimal amount) {
        String detailUrl = frontendBaseUrl + "/reservations/" + r.getId();
        String reviewUrl = frontendBaseUrl + "/reservations/" + r.getId() + "/review";

        String roomDetails = reservationService.getReservationsRoomByReservationsId(r.getId()).stream()
                .map(rr -> "Phòng " + rr.getRoomId().getRoomNumber()
                        + " (Nhận: " + r.getCheckIn() + ", Trả: " + r.getCheckOut() + ")")
                .collect(Collectors.joining("<br/>"));
        String serviceDetails = reservationService.getServiceOrders(r.getId()).stream()
                .map(order -> order.getServiceId().getName() + " (" + order.getAmount() + " VND)")
                .collect(Collectors.joining("<br/>"));

        String subject = "Smart Hotel - Xác nhận đặt phòng #" + r.getId();

        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><head>")
                .append("<meta charset='UTF-8'/>")
                .append("<title>").append(subject).append("</title>")
                .append("</head><body style='font-family: Arial, sans-serif; background-color:#f5f5f5; padding:16px;'>")
                .append("<div style='max-width:600px;margin:0 auto;background:#ffffff;border-radius:8px;overflow:hidden;border:1px solid #e0e0e0;'>")
                .append("<div style='background:#0d6efd;color:#fff;padding:16px 20px;'>")
                .append("<h2 style='margin:0;font-size:20px;'>Smart Hotel</h2>")
                .append("<p style='margin:4px 0 0;'>Xác nhận đặt phòng thành công</p>")
                .append("</div>")
                .append("<div style='padding:16px 20px;'>")
                .append("<p>Chào quý khách,</p>")
                .append("<p>Cảm ơn bạn đã đặt phòng tại <strong>Smart Hotel</strong>. Thông tin đặt phòng của bạn như sau:</p>")
                .append("<p><strong>Mã đặt phòng:</strong> #").append(r.getId()).append("<br/>")
                .append("<strong>Tổng thanh toán:</strong> ")
                .append(amount != null ? amount.toString() : "").append(" VND<br/>")
                .append("<strong>Ngày nhận phòng:</strong> ").append(r.getCheckIn()).append("<br/>")
                .append("<strong>Ngày trả phòng:</strong> ").append(r.getCheckOut()).append("</p>")
                .append("<hr style='border:none;border-top:1px solid #e0e0e0;margin:12px 0;'/>")
                .append("<h4 style='margin:0 0 8px;'>Phòng</h4>")
                .append("<p style='margin:0;'>").append(roomDetails).append("</p>");

        if (!serviceDetails.isEmpty()) {
            body.append("<h4 style='margin:16px 0 8px;'>Dịch vụ</h4>")
                    .append("<p style='margin:0;'>").append(serviceDetails).append("</p>");
        }

        body.append("<hr style='border:none;border-top:1px solid #e0e0e0;margin:12px 0;'/>")
                .append("<p><strong>Nhắc check-in:</strong> Bạn vui lòng đến quầy lễ tân vào ngày ")
                .append(r.getCheckIn()).append(" để nhận phòng.</p>")
                .append("<p>")
                .append("<a href='").append(detailUrl).append("' ")
                .append("style='display:inline-block;padding:10px 16px;background:#0d6efd;color:#fff;text-decoration:none;border-radius:4px;margin-right:8px;'>")
                .append("Xem chi tiết đặt phòng</a>")
                .append("<a href='").append(reviewUrl).append("' ")
                .append("style='display:inline-block;padding:10px 16px;background:#198754;color:#fff;text-decoration:none;border-radius:4px;'>")
                .append("Viết đánh giá sau khi trả phòng</a>")
                .append("</p>")
                .append("<p style='margin-top:16px;'>Trân trọng,<br/>Smart Hotel</p>")
                .append("</div></div></body></html>");

        return new String[]{subject, body.toString()};
    }

}
