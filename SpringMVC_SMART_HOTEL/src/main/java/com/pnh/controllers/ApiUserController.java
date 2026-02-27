/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.controllers;

import com.pnh.pojo.CustomerProfiles;
import com.pnh.pojo.Users;
import com.pnh.services.UserService;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.pnh.utils.JwtUtils;
import java.security.Principal;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
@PropertySource("classpath:gugle.properties")
@PropertySource(value = "classpath:gugle-local.properties", ignoreResourceNotFound = true)
public class ApiUserController {
    private static final Logger LOG = Logger.getLogger(ApiUserController.class.getName());

    @Value("${client_id}")
    private String GOOGLE_CLIENT_ID;
    
    @Autowired
    private UserService userDetailsService;

    @PostMapping(path = "/users",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Users> create(@RequestParam Map<String, String> params, 
            @RequestParam(value = "avatar") MultipartFile avatar) {
        return new ResponseEntity<>(this.userDetailsService.addUser(params, avatar), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users u) {
        try {
            if (this.userDetailsService.authenticate(u.getUsername(), u.getPassword())) {
                String token = JwtUtils.generateToken(u.getUsername());
                return ResponseEntity.ok().body(Collections.singletonMap("token", token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
            }
        } catch (Exception e) {
            LOG.log(java.util.logging.Level.SEVERE, "Login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }

    @RequestMapping("/secure/profile")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<Users> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userDetailsService.getUserByUsername(principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/secure/customer-profile")
    @ResponseBody
    public ResponseEntity<CustomerProfiles> getCustomerProfile(Principal principal) {
        CustomerProfiles profile = this.userDetailsService.getCustomerProfile(principal.getName());
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    

    @PostMapping("/login/google")
    public ResponseEntity<?> verifyGoogleToken(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("token");
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idTokenString;
            Map<?, ?> tokenInfo = restTemplate.getForObject(url, Map.class);
            if (tokenInfo != null && GOOGLE_CLIENT_ID.equals(String.valueOf(tokenInfo.get("aud")))) {
                String email = String.valueOf(tokenInfo.get("email"));
                String name = tokenInfo.get("name") != null ? String.valueOf(tokenInfo.get("name")) : email;
                String pictureUrl = tokenInfo.get("picture") != null ? String.valueOf(tokenInfo.get("picture")) : null;

                Users user = null;
                try {
                    user = userDetailsService.getUserByEmail(email);
                } catch (Exception ex) {
                    user = null;
                }
                if (user == null) {
                    user = userDetailsService.createUserFromGoogle(email, name, pictureUrl);
                }

                String token = JwtUtils.generateToken(user.getUsername());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("email", email);
                response.put("fullname", name);
                response.put("avatar", pictureUrl);

                return ResponseEntity.ok(response);
            } else {
                LOG.warning("Invalid ID token or audience mismatch");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token.");
            }
        } catch (HttpClientErrorException hce) {
            String respBody = hce.getResponseBodyAsString();
            LOG.warning("Google tokeninfo HTTP error: " + respBody);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Google tokeninfo error: " + respBody);
        } catch (Exception e) {
            LOG.log(java.util.logging.Level.SEVERE, "Google login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/public/google-client-id")
    public ResponseEntity<?> getGoogleClientId() {
        Map<String, String> res = new HashMap<>();
        res.put("client_id", GOOGLE_CLIENT_ID);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body != null ? body.get("email") : null;
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không được để trống."));
        }
        boolean sent = userDetailsService.requestPasswordReset(email.trim());
        if (sent) {
            return ResponseEntity.ok(Map.of("message", "Nếu email tồn tại, bạn sẽ nhận được link đặt lại mật khẩu."));
        }
        return ResponseEntity.ok(Map.of("message", "Nếu email tồn tại, bạn sẽ nhận được link đặt lại mật khẩu."));
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body != null ? body.get("token") : null;
        String newPassword = body != null ? body.get("newPassword") : null;
        if (token == null || token.isBlank() || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token và mật khẩu mới (ít nhất 6 ký tự) là bắt buộc."));
        }
        boolean ok = userDetailsService.resetPassword(token.trim(), newPassword);
        if (ok) {
            return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập bằng mật khẩu mới."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Link không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu gửi lại email."));
    }
}

