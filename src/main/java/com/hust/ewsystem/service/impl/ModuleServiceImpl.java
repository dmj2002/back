package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.Module;
import com.hust.ewsystem.mapper.ModuleMapper;
import com.hust.ewsystem.service.ModuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2024-11-26  10:56
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> implements ModuleService {
}
