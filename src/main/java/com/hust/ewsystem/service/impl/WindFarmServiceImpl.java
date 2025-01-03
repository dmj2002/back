package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.WindFarm;
import com.hust.ewsystem.mapper.WindFarmMapper;
import com.hust.ewsystem.service.WindFarmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2025-01-03  10:23
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class WindFarmServiceImpl extends ServiceImpl<WindFarmMapper, WindFarm> implements WindFarmService {
}
