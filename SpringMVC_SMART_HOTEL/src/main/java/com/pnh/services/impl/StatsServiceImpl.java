/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.repositories.StatsRepository;
import com.pnh.services.StatsService;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ADMIN
 */
@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsRepository statsRepository;

    @Override
    public List<Map<String, Object>> getRevenueChart(String time, int year, String period) {
        List<Object[]> raw = this.statsRepository.statsRevenueByTime(time, year);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object[] row : raw) {
            int p = ((Number) row[0]).intValue();
            if ("ALL".equalsIgnoreCase(period)
                    || ("MONTH".equalsIgnoreCase(time) && String.valueOf(p).equals(period))
                    || ("QUARTER".equalsIgnoreCase(time) && String.valueOf(p).equals(period))
                    || ("YEAR".equalsIgnoreCase(time) && String.valueOf(p).equals(period))) {
                Map<String, Object> m = new HashMap<>();
                m.put("period", p);
                m.put("revenue", row[1]);
                out.add(m);
            }
        }
        out.sort((a,b) -> Integer.compare(((Number)a.get("period")).intValue(), ((Number)b.get("period")).intValue()));
        return out;
    }

    @Override
    public List<Map<String, Object>> getOccupancyPercentByMonth(int year, String time, String period) {
        List<Object[]> occRaw = this.statsRepository.statsOccupancyByMonth(year);
        long numRooms = this.statsRepository.countAllRooms();
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object[] row : occRaw) {
            int month = ((Number) row[0]).intValue();
            boolean accept = true;
            if (!"ALL".equalsIgnoreCase(period)) {
                if ("MONTH".equalsIgnoreCase(time)) {
                    accept = String.valueOf(month).equals(period);
                } else if ("QUARTER".equalsIgnoreCase(time)) {
                    try {
                        int q = Integer.parseInt(period);
                        int start = (q - 1) * 3 + 1;
                        int end = q * 3;
                        accept = month >= start && month <= end;
                    } catch (NumberFormatException ex) {
                        accept = true;
                    }
                }
            }
            if (accept) {
                long occupied = ((Number) row[1]).longValue();
                int daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth();
                double percent = (numRooms == 0) ? 0.0 : (occupied * 100.0) / (numRooms * daysInMonth);
                // DEBUG: In công thức occupancy để đối chiếu
                System.out.printf(
                        "[OCC DEBUG] year=%d month=%d | occupied(room-nights)=%d | rooms=%d | days=%d | denom=%d | percent=%.4f%%%n",
                        year,
                        month,
                        occupied,
                        numRooms,
                        daysInMonth,
                        (numRooms * daysInMonth),
                        percent
                );
                Map<String, Object> m = new HashMap<>();
                m.put("month", month);
                m.put("occupancyPercent", percent);
                out.add(m);
            }
        }
        out.sort((a,b) -> Integer.compare(((Number)a.get("month")).intValue(), ((Number)b.get("month")).intValue()));
        return out;
    }

    @Override
    public List<Map<String, Object>> getAvgRatingByMonthChart(int year, String time, String period) {
        List<Object[]> raw = this.statsRepository.statsAvgRatingByMonth(year);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object[] row : raw) {
            int month = ((Number) row[0]).intValue();
            boolean accept = true;
            if (!"ALL".equalsIgnoreCase(period)) {
                if ("MONTH".equalsIgnoreCase(time)) {
                    accept = String.valueOf(month).equals(period);
                } else if ("QUARTER".equalsIgnoreCase(time)) {
                    try {
                        int q = Integer.parseInt(period);
                        int start = (q - 1) * 3 + 1;
                        int end = q * 3;
                        accept = month >= start && month <= end;
                    } catch (NumberFormatException ex) {
                        accept = true;
                    }
                }
            }
            if (accept) {
                Map<String, Object> m = new HashMap<>();
                m.put("month", month);
                m.put("avgRating", ((Number) row[1]).doubleValue());
                m.put("count", ((Number) row[2]).intValue());
                out.add(m);
            }
        }
        out.sort((a,b) -> Integer.compare(((Number)a.get("month")).intValue(), ((Number)b.get("month")).intValue()));
        return out;
    }
}
