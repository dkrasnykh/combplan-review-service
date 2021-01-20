package com.orioninc.combplanreviewservice.streamconsumer;

import com.orioninc.combplanreviewservice.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

public class UserStore {
    public static List<UserDto> users = new ArrayList<>();

    public static void addUser(UserDto user){
        users.add(user);
    }
}
