/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pnh.services;

import com.pnh.pojo.RoomTypes;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface RoomTypeService {
    List<RoomTypes> getRoomTypes();
    RoomTypes getById(Long id);
    RoomTypes addOrUpdate(RoomTypes rt);
    void delete(Long id);
}
