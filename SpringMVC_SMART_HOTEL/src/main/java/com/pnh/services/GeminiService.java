package com.pnh.services;

/**
 * Gọi Google Gemini API để sinh câu trả lời từ context (RAG-style).
 * Trả về null nếu không cấu hình key hoặc lỗi.
 */
public interface GeminiService {
    /**
     * @param systemContext Thông tin khách sạn (phòng, dịch vụ, chính sách) để model bám sát.
     * @param userMessage   Câu hỏi của khách.
     * @return Câu trả lời từ LLM, hoặc null nếu lỗi/không có key.
     */
    String generateReply(String systemContext, String userMessage);
}
