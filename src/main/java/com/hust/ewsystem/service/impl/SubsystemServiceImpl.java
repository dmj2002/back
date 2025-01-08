package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.Subsystem;
import com.hust.ewsystem.mapper.SubSystemMapper;
import com.hust.ewsystem.service.SubsystemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2025-01-08  14:49
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class SubsystemServiceImpl extends ServiceImpl<SubSystemMapper, Subsystem> implements SubsystemService {
}
