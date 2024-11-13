package com.hust.ewsystem;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class test {

    public static void main(String[] args) {
        String time = "2021-06-01 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse( time, formatter);
        // 使用格式化器输出，不带 "T"
        String formattedTime = startTime.format(formatter);

        System.out.println(formattedTime); // 输出为 "2021-06-01 00:00:00"
    }
}
