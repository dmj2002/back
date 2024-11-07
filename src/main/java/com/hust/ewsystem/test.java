package com.hust.ewsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class test {

    private static final Logger LOGGER = LoggerFactory.getLogger(test.class);
    public static void main(String[] args) {
        Integer modelId = 10000;
        String s = "M" + String.format("%04d", modelId);
        System.out.println(s);
    }
}
