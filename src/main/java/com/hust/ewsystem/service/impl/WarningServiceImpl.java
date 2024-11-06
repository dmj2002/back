package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.Warnings;
import com.hust.ewsystem.mapper.WarningMapper;
import com.hust.ewsystem.service.WarningService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WarningServiceImpl extends ServiceImpl<WarningMapper, Warnings> implements WarningService {
}
