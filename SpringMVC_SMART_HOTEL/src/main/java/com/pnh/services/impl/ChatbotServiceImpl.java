package com.pnh.services.impl;

import com.pnh.dto.ChatbotReplyResult;
import com.pnh.pojo.RoomTypes;
import com.pnh.pojo.Services;
import com.pnh.services.ChatbotService;
import com.pnh.services.GeminiService;
import com.pnh.services.RoomTypeService;
import com.pnh.services.ServiceService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Chatbot hybrid: rule-based (keyword + DB) cho câu chuẩn; LLM (Gemini) cho câu mở.
 * Nếu không cấu hình Gemini API key thì chỉ dùng rule-based.
 */
@Service
public class ChatbotServiceImpl implements ChatbotService {

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private GeminiService geminiService;

    @Override
    public ChatbotReplyResult reply(String message) {
        if (message == null || message.isBlank()) {
            return new ChatbotReplyResult(
                    "Xin chào! Tôi có thể giúp gì cho bạn? Ví dụ: hỏi về loại phòng, giá dịch vụ, chính sách hủy phòng.",
                    "rule");
        }
        String m = message.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[\\p{Punct}]", " ");

        // Chào hỏi
        if (matches(m, "chào", "hello", "hi", "xin chào", "alô")) {
            return new ChatbotReplyResult(
                    "Xin chào! Tôi là trợ lý ảo Smart Hotel. Bạn muốn hỏi về phòng, dịch vụ hay chính sách?",
                    "rule");
        }

        // Loại phòng / view biển
        if (matches(m, "phòng", "room", "loại phòng", "view biển", "view", "biển", "có phòng")) {
            return new ChatbotReplyResult(buildRoomTypesReply(), "rule");
        }

        // Giá phòng
        if (matches(m, "giá phòng", "giá", "bao nhiêu", "price", "phí")) {
            if (matches(m, "phòng", "room") || !matches(m, "dịch vụ", "service", "buffet", "spa", "sân bay")) {
                return new ChatbotReplyResult(buildRoomTypesReply(), "rule");
            }
        }

        // Dịch vụ: buffet, breakfast, spa, airport
        if (matches(m, "dịch vụ", "service", "buffet", "breakfast", "sáng", "spa", "sân bay", "airport", "đưa đón")) {
            return new ChatbotReplyResult(buildServicesReply(), "rule");
        }

        // Giá buffet / breakfast
        if (matches(m, "buffet", "breakfast", "bữa sáng", "giá buffet", "giá sáng")) {
            return new ChatbotReplyResult(buildServicePriceReply("Breakfast", "buffet sáng"), "rule");
        }

        // Chính sách hủy
        if (matches(m, "hủy", "cancel", "chính sách", "policy", "hoàn tiền")) {
            return new ChatbotReplyResult(
                    "Chính sách hủy phòng:\n" +
                            "• Hủy trước 24h: hoàn 100% (trừ phí xử lý 50.000 VND)\n" +
                            "• Hủy trong 24h: hoàn 50%\n" +
                            "• No-show: không hoàn tiền\n\n" +
                            "Bạn có thể hủy qua trang Chi tiết đặt phòng hoặc liên hệ quầy lễ tân.",
                    "rule");
        }

        // Đặt phòng
        if (matches(m, "đặt phòng", "book", "reservation", "đặt")) {
            return new ChatbotReplyResult(
                    "Bạn có thể đặt phòng qua:\n" +
                            "• Trang chủ: bấm nút 'Đặt phòng ngay'\n" +
                            "• Menu: chọn 'ĐẶT PHÒNG'\n" +
                            "• Link trực tiếp: /booking\n\n" +
                            "Chọn ngày, loại phòng, dịch vụ bổ sung rồi thanh toán. Cần hỗ trợ thêm không?",
                    "rule");
        }

        // Liên hệ
        if (matches(m, "liên hệ", "contact", "hotline", "số điện thoại", "địa chỉ")) {
            return new ChatbotReplyResult(
                    "Thông tin liên hệ Smart Hotel:\n" +
                            "• Địa chỉ: 123 Nguyễn Huệ, Quận 1, TP.HCM\n" +
                            "• Hotline: 1900 1234\n" +
                            "• Email: contact@smarthotel.vn\n" +
                            "• Xem bản đồ: trang Liên hệ",
                    "rule");
        }

        // Câu không khớp rule -> thử LLM (Gemini) với context từ DB
        String context = buildLlmContext();
        String llmReply = geminiService != null ? geminiService.generateReply(context, message) : null;
        if (llmReply != null && !llmReply.isBlank()) {
            return new ChatbotReplyResult(llmReply, "llm");
        }

        // Fallback khi không có LLM hoặc LLM lỗi
        return new ChatbotReplyResult(
                "Xin lỗi, tôi chưa hiểu rõ. Bạn có thể thử:\n" +
                        "• \"Có phòng view biển không?\"\n" +
                        "• \"Giá buffet sáng?\"\n" +
                        "• \"Chính sách hủy phòng?\"\n" +
                        "• \"Liên hệ khách sạn?\"",
                "rule");
    }

    private String buildLlmContext() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildRoomTypesReply()).append("\n\n");
        sb.append(buildServicesReply()).append("\n\n");
        sb.append("Chính sách hủy: Hủy trước 24h hoàn 100% (trừ phí 50.000 VND); trong 24h hoàn 50%; no-show không hoàn. Liên hệ: 123 Nguyễn Huệ, Q1, TP.HCM; Hotline 1900 1234; contact@smarthotel.vn.");
        return sb.toString();
    }

    private boolean matches(String text, String... keywords) {
        for (String k : keywords) {
            if (text.contains(k.toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) return "—";
        return String.format("%,d", price.longValue()) + " VND";
    }

    private String buildRoomTypesReply() {
        List<RoomTypes> list = roomTypeService.getRoomTypes();
        if (list == null || list.isEmpty()) {
            return "Hiện chưa có thông tin loại phòng. Vui lòng liên hệ quầy lễ tân.";
        }
        StringBuilder sb = new StringBuilder("Các loại phòng hiện có:\n\n");
        for (RoomTypes rt : list) {
            sb.append("• ").append(rt.getName())
                    .append(": ").append(formatPrice(rt.getPrice()))
                    .append("/đêm, ").append(rt.getCapacity()).append(" khách");
            if (rt.getDescription() != null && !rt.getDescription().isBlank()) {
                sb.append(" - ").append(rt.getDescription());
            }
            sb.append("\n");
        }
        sb.append("\nBạn có thể xem chi tiết và đặt phòng tại trang chủ.");
        return sb.toString();
    }

    private String buildServicesReply() {
        List<Services> list = serviceService.getServices();
        if (list == null || list.isEmpty()) {
            return "Hiện chưa có thông tin dịch vụ. Vui lòng liên hệ quầy lễ tân.";
        }
        StringBuilder sb = new StringBuilder("Dịch vụ bổ sung:\n\n");
        for (Services s : list) {
            sb.append("• ").append(s.getName())
                    .append(": ").append(formatPrice(s.getPrice()));
            if (s.getDescription() != null && !s.getDescription().isBlank()) {
                sb.append(" - ").append(s.getDescription());
            }
            sb.append("\n");
        }
        sb.append("\nBạn có thể thêm dịch vụ khi đặt phòng.");
        return sb.toString();
    }

    private String buildServicePriceReply(String nameContains, String label) {
        List<Services> list = serviceService.getServices();
        if (list != null) {
            for (Services s : list) {
                if (s.getName() != null && s.getName().toLowerCase(Locale.ROOT).contains(nameContains.toLowerCase(Locale.ROOT))) {
                    return "Giá " + label + ": " + formatPrice(s.getPrice()) + ". Bạn có thể thêm khi đặt phòng.";
                }
            }
        }
        return "Hiện chưa có thông tin giá " + label + ". Vui lòng liên hệ quầy lễ tân.";
    }
}
