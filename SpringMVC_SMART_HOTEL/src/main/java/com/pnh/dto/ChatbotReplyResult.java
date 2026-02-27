package com.pnh.dto;

/**
 * Kết quả trả lời chatbot: nội dung + nguồn (rule-based hoặc LLM).
 * Dùng để frontend hiển thị badge "Answered by AI" khi replySource = "llm".
 */
public class ChatbotReplyResult {
    private final String reply;
    private final String replySource; // "rule" | "llm"

    public ChatbotReplyResult(String reply, String replySource) {
        this.reply = reply;
        this.replySource = replySource != null ? replySource : "rule";
    }

    public String getReply() {
        return reply;
    }

    public String getReplySource() {
        return replySource;
    }
}
