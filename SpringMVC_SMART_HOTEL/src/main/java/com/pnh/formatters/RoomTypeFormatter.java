/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.formatters;


import com.pnh.pojo.RoomTypes;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author ADMIN
 */
public class RoomTypeFormatter implements Formatter<RoomTypes> {

    @Override
    public String print(RoomTypes roomtypes, Locale locale) {
        return String.valueOf(roomtypes.getId());
    }

    @Override
    public RoomTypes parse(String roomtypeId, Locale locale) throws ParseException {
        RoomTypes rt = new RoomTypes();
        rt.setId(Long.valueOf(roomtypeId));
        return rt;
    }
}
