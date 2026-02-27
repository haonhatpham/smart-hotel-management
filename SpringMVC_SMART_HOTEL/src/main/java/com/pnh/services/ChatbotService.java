package com.pnh.services;

import com.pnh.dto.ChatbotReplyResult;

/**
 * Chatbot hybrid: rule-based (giá, chính sách, DB) + LLM (Gemini) cho câu hỏi mở.
 */
public interface ChatbotService {
    ChatbotReplyResult reply(String message);
}
