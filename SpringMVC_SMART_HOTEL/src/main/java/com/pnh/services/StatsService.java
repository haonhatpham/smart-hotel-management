/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pnh.services;

import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface StatsService {
    // View-ready data
    List<java.util.Map<String, Object>> getRevenueChart(String time, int year, String period);
    List<java.util.Map<String, Object>> getOccupancyPercentByMonth(int year, String time, String period);
    List<java.util.Map<String, Object>> getAvgRatingByMonthChart(int year, String time, String period);
}
