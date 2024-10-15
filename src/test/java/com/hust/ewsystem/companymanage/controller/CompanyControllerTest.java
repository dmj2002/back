package com.hust.ewsystem.companymanage.controller;


import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.controller.CompanyController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class CompanyControllerTest {

    @Resource
    private CompanyController companyController;

    @Test
    void addCompany() {
        String emptyString = "";
        String nullString = null;
        if (emptyString.isEmpty()) {
            System.out.println("emptyString is empty");
            System.out.println(emptyString);
        }

        if (nullString == null) {
            System.out.println("nullString is null");
            System.out.println(nullString);
        }
    }

    @Test
    void getCompany() {
        EwsResult<?> company = companyController.getCompany("0101");
        System.out.println(company.getCode());
        System.out.println(company.getMessage());
        System.out.println(company.getResult());

    }

    @Test
    void updateCompany() {
    }

    @Test
    void deleteCompany() {
    }

    @Test
    void listCompany() {
    }
}