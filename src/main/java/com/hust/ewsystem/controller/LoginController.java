package com.hust.ewsystem.controller;

import com.hust.ewsystem.common.result.EwsResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @PostMapping("/login")
    public EwsResult<?> login(@RequestBody Map<String,Object> params) {
        String account = (String) params.get("account");
        String password = (String) params.get("password");
        Map<String, Object> accountRoles = new HashMap<>();
        accountRoles.put("15754803041:123456", "B37");
        accountRoles.put("13150855418:123456", "C6");
        accountRoles.put("18047141606:123456", "B17");
        String key = account + ":" + password;
        if (accountRoles.containsKey(key)) {
            Map<String, Object> res = new HashMap<>();
            res.put("role", accountRoles.get(key));
            return EwsResult.OK("登录成功",res);
        } else {
            return EwsResult.error("账户或者密码错误");
        }
    }
}
