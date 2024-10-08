package com.hust.ewsystem.usermanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.usermanage.entity.User;
import com.hust.ewsystem.usermanage.mapper.UserMapper;
import com.hust.ewsystem.usermanage.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
