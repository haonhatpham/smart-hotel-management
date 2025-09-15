/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pnh.repositories;

import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface StatsRepository {

    List<Object[]> statsRevenueByTime(String time, int year);

    List<Object[]> statsOccupancyByTime(String time, int year);

     List<Object[]> statsAvgRatingByTime(String time, int year);

}
