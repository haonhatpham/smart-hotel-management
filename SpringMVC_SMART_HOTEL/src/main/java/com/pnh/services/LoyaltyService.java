package com.pnh.services;

import com.pnh.pojo.LoyaltyLog;
import com.pnh.pojo.CustomerProfiles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface LoyaltyService {
    /** Tích điểm khi thanh toán thành công: 1 điểm / 10.000 VND */
    void addPoints(Long userId, Long reservationId, BigDecimal totalAmount);
    /** Thông tin loyalty: points, tier, history */
    Map<String, Object> getLoyaltyInfo(Long userId);
    /** Tính hạng từ điểm: BRONZE, SILVER, GOLD */
    String getTierFromPoints(int points);

    /**
     * Đổi điểm để giảm giá cho một hóa đơn.
     * Rule demo:
     *  - 1 điểm = 1.000 VND
     *  - Tối đa 20% giá trị hóa đơn được giảm bằng điểm
     * Hàm sẽ tự trừ điểm và ghi log nếu áp dụng giảm giá, trả về số tiền sau giảm.
     */
    BigDecimal applyRedemption(Long userId, Long reservationId, BigDecimal originalAmount);
}
