package com.pnh.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnh.services.GeminiService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import java.util.logging.Logger;

/**
 * Gọi Google Gemini API (REST) để sinh câu trả lời.
 * Dùng system instruction = context khách sạn (RAG), user message = câu hỏi.
 */
@Service
public class GeminiServiceImpl implements GeminiService {

    private static final Logger LOG = Logger.getLogger(GeminiServiceImpl.class.getName());

    // gemini-2.0-flash là model hỗ trợ generateContent với API key; 429 = hết quota free
    private static final String GEMINI_MODEL = "gemini-2.0-flash";
    private static final String GEMINI_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent";
    private static final int RETRY_DELAY_MS = 45_000;

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generateReply(String systemContext, String userMessage) {
        String key = (apiKey != null ? apiKey : "").trim();
        if (key.isEmpty()) {
            key = System.getenv("GEMINI_API_KEY");
            if (key != null) key = key.trim();
        }
        if (key == null || key.isEmpty()) {
            return null;
        }
        String url = GEMINI_URL_TEMPLATE + "?key=" + key;
        String systemInstruction = "Bạn là trợ lý ảo của Smart Hotel. Trả lời ngắn gọn, thân thiện bằng tiếng Việt. "
                + "Chỉ dựa vào thông tin sau đây để trả lời về giá, phòng, dịch vụ, chính sách. "
                + "Nếu câu hỏi ngoài phạm vi, nói lịch sự rằng bạn chỉ hỗ trợ thông tin khách sạn.\n\n"
                + "--- THÔNG TIN KHÁCH SẠN ---\n" + (systemContext != null ? systemContext : "");

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", userMessage != null ? userMessage : ""))
                )),
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", systemInstruction))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 1024
                )
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        for (int attempt = 0; attempt <= 1; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return extractTextFromGeminiResponse(response.getBody());
                }
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode().value() == 429 && attempt == 0) {
                    LOG.warning("Gemini 429 Too Many Requests, retry after " + (RETRY_DELAY_MS / 1000) + "s");
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                    continue;
                }
                LOG.warning("Gemini API call failed: " + e.getStatusCode() + " " + e.getMessage());
            } catch (Exception e) {
                LOG.warning("Gemini API call failed: " + e.getMessage());
            }
            break;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromGeminiResponse(String json) {
        try {
            Map<String, Object> root = objectMapper.readValue(json, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) root.get("candidates");
            if (candidates == null || candidates.isEmpty()) return null;
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) return null;
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) return null;
            Object text = parts.get(0).get("text");
            return text != null ? text.toString().trim() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
