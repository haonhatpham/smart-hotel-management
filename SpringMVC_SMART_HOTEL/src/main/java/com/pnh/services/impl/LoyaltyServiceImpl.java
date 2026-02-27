package com.pnh.services.impl;

import com.pnh.pojo.CustomerProfiles;
import com.pnh.pojo.LoyaltyLog;
import com.pnh.pojo.Users;
import com.pnh.repositories.LoyaltyLogRepository;
import com.pnh.services.LoyaltyService;
import com.pnh.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    private static final int POINTS_PER_10K = 1;
    private static final int POINT_VALUE = 1_000; // 1 điểm = 1.000 VND
    private static final int MAX_DISCOUNT_PERCENT = 20; // tối đa 20% hóa đơn
    private static final int BRONZE_MAX = 999;
    private static final int SILVER_MAX = 4999;

    @Autowired
    private UserService userService;

    @Autowired
    private LoyaltyLogRepository loyaltyLogRepository;

    @Override
    @Transactional
    public void addPoints(Long userId, Long reservationId, BigDecimal totalAmount) {
        if (userId == null || totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) return;
        int points = totalAmount.intValue() / 10_000 * POINTS_PER_10K;
        if (points <= 0) return;

        CustomerProfiles profile = userService.getCustomerProfileByUserId(userId);
        if (profile == null) {
            Users user = userService.getById(userId);
            if (user == null) return;
            profile = new CustomerProfiles();
            profile.setUserId(user);
            profile.setLoyaltyPoint(0);
            userService.saveCustomerProfile(profile);
        }
        int newTotal = profile.getLoyaltyPoint() + points;
        profile.setLoyaltyPoint(newTotal);
        userService.saveCustomerProfile(profile);

        LoyaltyLog log = new LoyaltyLog();
        log.setUserId(userId);
        log.setPoints(points);
        log.setReservationId(reservationId);
        log.setReason("Tích điểm từ đặt phòng #" + reservationId);
        log.setCreatedAt(new Date());
        loyaltyLogRepository.save(log);
    }

    @Override
    public Map<String, Object> getLoyaltyInfo(Long userId) {
        Map<String, Object> out = new HashMap<>();
        out.put("points", 0);
        out.put("tier", "BRONZE");
        out.put("history", Collections.emptyList());

        if (userId == null) return out;

        CustomerProfiles profile = userService.getCustomerProfileByUserId(userId);
        int points = profile != null ? profile.getLoyaltyPoint() : 0;
        out.put("points", points);
        out.put("tier", getTierFromPoints(points));

        List<LoyaltyLog> logs = loyaltyLogRepository.findByUserIdOrderByCreatedAtDesc(userId, 20);
        List<Map<String, Object>> history = new ArrayList<>();
        for (LoyaltyLog log : logs) {
            Map<String, Object> e = new HashMap<>();
            e.put("points", log.getPoints());
            e.put("reason", log.getReason());
            e.put("reservationId", log.getReservationId());
            e.put("createdAt", log.getCreatedAt());
            history.add(e);
        }
        out.put("history", history);
        return out;
    }

    @Override
    public String getTierFromPoints(int points) {
        if (points <= BRONZE_MAX) return "BRONZE";
        if (points <= SILVER_MAX) return "SILVER";
        return "GOLD";
    }

    @Override
    @Transactional
    public BigDecimal applyRedemption(Long userId, Long reservationId, BigDecimal originalAmount) {
        if (userId == null || originalAmount == null || originalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return originalAmount != null ? originalAmount : BigDecimal.ZERO;
        }

        CustomerProfiles profile = userService.getCustomerProfileByUserId(userId);
        if (profile == null || profile.getLoyaltyPoint() <= 0) {
            return originalAmount;
        }

        int availablePoints = profile.getLoyaltyPoint();

        // Tối đa 20% hóa đơn được giảm bằng điểm
        BigDecimal maxDiscountByPercent = originalAmount
                .multiply(BigDecimal.valueOf(MAX_DISCOUNT_PERCENT))
                .divide(BigDecimal.valueOf(100));
        BigDecimal maxDiscountByPoints = BigDecimal.valueOf((long) availablePoints * POINT_VALUE);

        BigDecimal discount = maxDiscountByPoints.min(maxDiscountByPercent);
        if (discount.compareTo(BigDecimal.ZERO) <= 0) {
            return originalAmount;
        }

        // Số điểm thực sự sử dụng (làm tròn xuống theo 1.000 VND/điểm)
        int pointsToUse = discount
                .divide(BigDecimal.valueOf(POINT_VALUE))
                .setScale(0, BigDecimal.ROUND_FLOOR)
                .intValue();

        if (pointsToUse <= 0) {
            return originalAmount;
        }

        // Cập nhật số điểm còn lại
        int remaining = availablePoints - pointsToUse;
        if (remaining < 0) remaining = 0;
        profile.setLoyaltyPoint(remaining);
        userService.saveCustomerProfile(profile);

        // Ghi log sử dụng điểm (points âm)
        LoyaltyLog log = new LoyaltyLog();
        log.setUserId(userId);
        log.setPoints(-pointsToUse);
        log.setReservationId(reservationId);
        log.setReason("Đổi " + pointsToUse + " điểm (giảm "
                + discount.longValue() + " VND) cho đặt phòng #" + reservationId);
        log.setCreatedAt(new Date());
        loyaltyLogRepository.save(log);

        return originalAmount.subtract(discount);
    }
}
