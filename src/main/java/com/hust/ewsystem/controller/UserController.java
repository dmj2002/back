package com.hust.ewsystem.controller;

import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.User;
import com.hust.ewsystem.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public EwsResult<Boolean> addUser(@RequestBody @Validated User user) {
        boolean result = userService.save(user);
        return result ? EwsResult.ok("添加成功") : EwsResult.error("添加失败");
    }

    @GetMapping("/get/{id}")
    public EwsResult<?> getUser(@PathVariable String id) {
        User user = userService.getById(id);
        return Objects.isNull(user) ? EwsResult.error("未找到用户") : EwsResult.ok(user);
    }

    @PutMapping("/update")
    public EwsResult<Boolean> updateUser(@RequestBody @Validated User user) {
        boolean result = userService.updateById(user);
        return result ? EwsResult.ok("更新成功") : EwsResult.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public EwsResult<Boolean> deleteUser(@PathVariable String id) {
        boolean result = userService.removeById(id);
        return result ? EwsResult.ok("删除成功") : EwsResult.error("删除失败");
    }

    @GetMapping("/list")
    public EwsResult<List<User>> listUser() {
        List<User> users = userService.list();
        return EwsResult.ok(users);
    }

}
