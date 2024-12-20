package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.StandPoint;
import com.hust.ewsystem.mapper.StandPointMapper;
import com.hust.ewsystem.service.StandPointService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2024-12-19  17:11
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class StandPointServiceImpl extends ServiceImpl<StandPointMapper, StandPoint> implements StandPointService {
}
