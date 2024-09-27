package com.hust.ewsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value={"com.hust.ewsystem.**.mapper*"})
public class EwSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EwSystemApplication.class, args);
    }

}
