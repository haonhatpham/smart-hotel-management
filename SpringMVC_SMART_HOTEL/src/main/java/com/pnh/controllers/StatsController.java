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
            @RequestParam(name = "period", defaultValue = "ALL") String period,
            Model model) {
        int y = (year != null) ? year : LocalDate.now().getYear();

        List<Map<String, Object>> revenueChart = this.statsService.getRevenueChart(time, y, period);
        List<Map<String, Object>> occChart = this.statsService.getOccupancyPercentByMonth(y, time, period);
        List<Map<String, Object>> ratingChart = this.statsService.getAvgRatingByMonthChart(y, time, period);

        model.addAttribute("time", time);
        model.addAttribute("selectedYear", y);
        model.addAttribute("revenueData", revenueChart);
        model.addAttribute("occupancyData", occChart);
        model.addAttribute("ratingData", ratingChart);
        model.addAttribute("period", period);
        return "stats";
    }
}
