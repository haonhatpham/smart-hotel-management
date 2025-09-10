/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.controllers;
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
/**
 *
 * @author User
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {
    @Autowired
    private UserService userDetailsService;
    
    @PostMapping(path = "/users", 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Users> create(@RequestParam Map<String, String> params, @RequestParam(value = "avatar") MultipartFile avatar) {
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage());
        }
    }

    @RequestMapping("/secure/profile")  
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<Users> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userDetailsService.getUserByUsername   (principal.getName()), HttpStatus.OK);
    }
}
