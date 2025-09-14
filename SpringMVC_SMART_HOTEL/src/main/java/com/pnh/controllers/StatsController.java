/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.controllers;

import com.pnh.services.StatsService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

/**
 *
 * @author ADMIN
 */
@Controller
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/stats")
    public String statsPage(
            @RequestParam(name = "time", defaultValue = "MONTH") String time,
            @RequestParam(name = "year", required = false) Integer year,
            Model model) {
        int y = (year != null) ? year : LocalDate.now().getYear();

        // Lấy dữ liệu từ repository
        List<Object[]> revenueData = this.statsService.statsRevenueByTime(time, y);
        List<Object[]> occupancyData = this.statsService.statsOccupancyByTime(time, y);
        List<Object[]> ratingData = this.statsService.statsAvgRatingByTime(time, y);

        // Chuyển đổi sang format phù hợp với frontend
        List<Map<String, Object>> revenueChart = convertRevenueData(revenueData, time);
        List<Map<String, Object>> occChart = convertOccupancyData(occupancyData, time);
        List<Map<String, Object>> ratingChart = convertRatingData(ratingData, time);

        model.addAttribute("time", time);
        model.addAttribute("selectedYear", y);
        model.addAttribute("revenueData", revenueChart);
        model.addAttribute("occupancyData", occChart);
        model.addAttribute("ratingData", ratingChart);
        return "stats";
    }

    private List<Map<String, Object>> convertRevenueData(List<Object[]> data, String time) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : data) {
            Map<String, Object> item = new HashMap<>();
            item.put("period", row[0]);
            item.put("revenue", ((Number) row[1]).doubleValue());
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> convertOccupancyData(List<Object[]> data, String time) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : data) {
            Map<String, Object> item = new HashMap<>();
            item.put("period", row[0]);
            item.put("month", row[0]); // Để tương thích với frontend
            item.put("bookedNights", ((Number) row[1]).longValue());
            item.put("totalPossibleNights", ((Number) row[2]).longValue());
            item.put("occupancyPercent", ((Number) row[3]).doubleValue());
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> convertRatingData(List<Object[]> data, String time) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : data) {
            Map<String, Object> item = new HashMap<>();
            item.put("period", row[0]);
            item.put("month", row[0]); // Để tương thích với frontend
            item.put("avgRating", ((Number) row[1]).doubleValue());
            item.put("count", ((Number) row[2]).longValue());
            result.add(item);
        }
        return result;
    }
}
