/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.services.impl;

import com.pnh.pojo.RoomTypes;
import com.pnh.repositories.RoomTypeRepository;
import com.pnh.services.RoomTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ADMIN
 */
@Service
public class RoomTypeServiceImpl  implements RoomTypeService{

    @Autowired
    private RoomTypeRepository roomTypeRepo;

    @Override
    public List<RoomTypes> getRoomTypes() {
        return this.roomTypeRepo.getRoomTypes();
    }

    @Override
    public RoomTypes getById(Long id) {
        return this.roomTypeRepo.getById(id);
    }

    @Override
    public RoomTypes addOrUpdate(RoomTypes rt) {
        return this.roomTypeRepo.addOrUpdate(rt);
    }

    @Override
    public void delete(Long id) {
        this.roomTypeRepo.delete(id);
    }
}
