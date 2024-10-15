package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.Company;
import com.hust.ewsystem.mapper.CompanyMapper;
import com.hust.ewsystem.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CompanyServiceImpl extends ServiceImpl <CompanyMapper, Company> implements CompanyService {
}
