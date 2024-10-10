package com.hust.ewsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//扫描对应包下所有 mapper 接口，并自动注册它们
@MapperScan(value={"com.hust.ewsystem.mapper*"})
public class EwSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EwSystemApplication.class, args
        );
    }

}
