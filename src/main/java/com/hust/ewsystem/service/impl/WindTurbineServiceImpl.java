package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.WindTurbine;
import com.hust.ewsystem.mapper.WindTurbineMapper;
import com.hust.ewsystem.service.WindTurbineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2025-01-03  11:09
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class WindTurbineServiceImpl extends ServiceImpl<WindTurbineMapper, WindTurbine> implements WindTurbineService {
}
