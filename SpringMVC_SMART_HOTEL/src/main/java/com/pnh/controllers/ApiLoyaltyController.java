package com.pnh.controllers;

import com.pnh.services.LoyaltyService;
import com.pnh.services.UserService;
import com.pnh.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiLoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @Autowired
    private UserService userService;

    @GetMapping("/secure/loyalty")
    public ResponseEntity<?> getMyLoyalty(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Chưa đăng nhập"));
        }
        Users user = userService.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Không tìm thấy user"));
        }
        Map<String, Object> info = loyaltyService.getLoyaltyInfo(user.getId());
        return ResponseEntity.ok(info);
    }
}
