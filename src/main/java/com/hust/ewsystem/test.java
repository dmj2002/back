package com.hust.ewsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class test {

    private static final Logger LOGGER = LoggerFactory.getLogger(test.class);
    public static void main(String[] args) {
        // 自定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 解析字符串为 LocalDateTime
        String dateTimeString = "2024-11-05 02:00:00";
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        // 格式化 LocalDateTime 为字符串
        String formattedDateTime = dateTime.format(formatter);

        System.out.println("Parsed LocalDateTime: " + dateTime);
        System.out.println("Formatted LocalDateTime: " + formattedDateTime);
    }
}
