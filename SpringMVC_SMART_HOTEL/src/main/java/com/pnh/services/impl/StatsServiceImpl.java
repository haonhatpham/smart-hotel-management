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
    public List<Object[]> statsRevenueByTime(String time, int year) {
        return statsRepository.statsRevenueByTime(time, year);
    }

    @Override
    public List<Object[]> statsOccupancyByTime(String time, int year) {
        return statsRepository.statsOccupancyByTime(time, year);
    }

    @Override
    public List<Object[]> statsAvgRatingByTime(String time, int year) {
        return statsRepository.statsAvgRatingByTime(time, year);
    }
}

