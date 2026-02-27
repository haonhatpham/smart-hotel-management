package com.pnh.controllers;

import com.pnh.dto.ChatbotReplyResult;
import com.pnh.services.ChatbotService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiChatController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {
        String message = body != null ? body.get("message") : null;
        ChatbotReplyResult result = chatbotService.reply(message);
        return ResponseEntity.ok(Map.of(
                "reply", result.getReply(),
                "replySource", result.getReplySource()
        ));
    }
}
