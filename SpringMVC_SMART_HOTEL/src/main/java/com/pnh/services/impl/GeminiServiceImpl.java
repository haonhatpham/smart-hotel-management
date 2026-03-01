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

    private static final String GEMINI_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";
    private static final int RETRY_DELAY_MS = 45_000;

    @Value("${gemini.api.key:}")
    private String apiKey;
    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private volatile boolean keyProbeLogged = false;

    @Override
    public String generateReply(String systemContext, String userMessage) {
        String propKey = (apiKey != null ? apiKey : "").trim();
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null) envKey = envKey.trim();

        String key = propKey;
        String keySource = "gemini.api.key";
        if (key.isEmpty()) {
            key = envKey;
            keySource = "GEMINI_API_KEY";
        }

        if (!keyProbeLogged) {
            if (key == null || key.isEmpty()) {
                LOG.warning("Gemini key missing: gemini.api.key/GEMINI_API_KEY đều rỗng.");
            } else {
                LOG.info("Gemini key loaded from " + keySource + ", preview=" + maskKey(key));
            }
            keyProbeLogged = true;
        }

        if (key == null || key.isEmpty()) {
            return null;
        }
        String useModel = (model != null && !model.isBlank()) ? model.trim() : "gemini-2.5-flash";
        String url = String.format(GEMINI_URL_TEMPLATE, useModel) + "?key=" + key;
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
                LOG.warning("Gemini API call failed: " + e.getStatusCode() + " " + e.getMessage()
                        + " | model=" + useModel + ", keySource=" + keySource + ", keyPreview=" + maskKey(key));
            } catch (Exception e) {
                LOG.warning("Gemini API call failed: " + e.getMessage());
            }
            break;
        }
        return null;
    }

    private String maskKey(String key) {
        if (key == null || key.isBlank()) return "<empty>";
        int len = key.length();
        if (len <= 10) return "<len=" + len + ">";
        return key.substring(0, 6) + "..." + key.substring(len - 4) + " (len=" + len + ")";
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
