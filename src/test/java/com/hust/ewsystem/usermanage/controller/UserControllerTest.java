package com.hust.ewsystem.usermanage.controller;

import com.hust.ewsystem.usermanage.entity.User;
import com.hust.ewsystem.usermanage.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Test
    void getUser() {
        User user = userService.getById("001");
        System.out.println(user);
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