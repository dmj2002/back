package com.hust.ewsystem.usermanage.controller;

import com.hust.ewsystem.entity.User;
import com.hust.ewsystem.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Test
    void getUser() {
        User user = userService.getById("006");
        if(Objects.isNull(user)) {
            System.out.println("User not found");
        } else {
            System.out.println(user);
        }
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void listUsers() {
    }
}