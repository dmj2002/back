package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.RealPoint;
import com.hust.ewsystem.mapper.RealPointMapper;
import com.hust.ewsystem.service.RealPointService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author piiaJet
 * @Create 2024/12/1716:41
 */
@Service
@Transactional
public class RealPointServiceImpl extends ServiceImpl<RealPointMapper, RealPoint> implements RealPointService {

}
