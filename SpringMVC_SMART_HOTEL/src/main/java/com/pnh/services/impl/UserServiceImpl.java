/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.pojo.PasswordResetToken;
import com.pnh.pojo.Users;
import com.pnh.pojo.CustomerProfiles;
import com.pnh.repositories.PasswordResetTokenRepository;
import com.pnh.repositories.UserRepository;
import com.pnh.services.UserService;
import com.pnh.utils.MailUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.util.UUID;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ADMIN
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordResetTokenRepository resetTokenRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @Value("${frontend.baseUrl:http://localhost:3000}")
    private String frontendBaseUrl;

    private static final int RESET_TOKEN_VALID_HOURS = 1;

    @Override
    public Users getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public Users getById(Long id) {
        return this.userRepo.getById(id);
    }

    //phương thức của UserDetailsService tự động gọi khi xác thực
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users u = this.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid username");
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + u.getRole()));

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }

    @Override
    public Users addUser(Map<String, String> params, MultipartFile avatar) {
        Users u = new Users();
        u.setFullName(params.get("fullname"));
        u.setEmail(params.get("email"));
        u.setPhone(params.get("phone"));
        u.setUsername(params.get("username"));
        u.setPassword(this.passwordEncoder.encode(params.get("password")));
        u.setRole("CUSTOMER");
        u.setEnabled(true);
        u.setCreatedAt(new Date());
        
        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return this.userRepo.addUser(u);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.userRepo.authenticate(username, password);
    }

    @Override
    public CustomerProfiles getCustomerProfile(String username) {
        return this.userRepo.getCustomerProfileByUsername(username);
    }

    @Override
    public CustomerProfiles getCustomerProfileByUserId(Long userId) {
        return this.userRepo.getCustomerProfileByUserId(userId);
    }

    @Override
    public CustomerProfiles saveCustomerProfile(CustomerProfiles profile) {
        return this.userRepo.saveCustomerProfile(profile);
    }

    @Override
    public Users getUserByEmail(String email) {
        return this.userRepo.getUserByEmail(email);
    }

    @Override
    public Users createUserFromGoogle(String email, String fullname, String avatarUrl) {
        Users existing = null;
        try {
            existing = this.userRepo.getUserByEmail(email);
        } catch (Exception ex) {
            existing = null;
        }
        if (existing != null) {
            return existing;
        }

        Users u = new Users();
        u.setEmail(email);
        u.setUsername(fullname != null ? fullname : email);
        u.setUsername(email);
        u.setRole("CUSTOMER");
        u.setPassword("1");
        u.setEnabled(true);
        u.setCreatedAt(new Date());
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            u.setAvatar(avatarUrl);
        }
        return this.userRepo.addUser(u);
    }

    @Override
    public boolean requestPasswordReset(String email) {
        if (email == null || email.isBlank()) return false;
        Users u = userRepo.getUserByEmail(email.trim());
        if (u == null) return false;
        resetTokenRepo.deleteExpired();
        String token = UUID.randomUUID().toString().replace("-", "");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, RESET_TOKEN_VALID_HOURS);
        PasswordResetToken prt = new PasswordResetToken();
        prt.setUserId(u.getId());
        prt.setToken(token);
        prt.setExpiresAt(cal.getTime());
        prt.setCreatedAt(new Date());
        resetTokenRepo.save(prt);
        String link = (frontendBaseUrl != null ? frontendBaseUrl.trim().replaceAll("/$", "") : "http://localhost:3000") + "/reset-password?token=" + token;
        String subject = "Smart Hotel - Đặt lại mật khẩu";
        String html = "<p>Xin chào,</p><p>Bạn đã yêu cầu đặt lại mật khẩu. Bấm vào link sau (có hiệu lực " + RESET_TOKEN_VALID_HOURS + " giờ):</p>"
                + "<p><a href=\"" + link + "\">Đặt lại mật khẩu</a></p><p>Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>";
        try {
            MailUtil.sendMail(u.getEmail(), subject, html);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank() || newPassword == null || newPassword.length() < 6) return false;
        PasswordResetToken prt = resetTokenRepo.findByToken(token.trim());
        if (prt == null || prt.getExpiresAt().before(new Date())) return false;
        Users u = userRepo.getById(prt.getUserId());
        if (u == null) return false;
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepo.updateUser(u);
        resetTokenRepo.deleteByToken(token);
        return true;
    }
}
