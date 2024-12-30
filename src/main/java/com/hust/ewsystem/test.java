package com.hust.ewsystem;


import com.hust.ewsystem.entity.Reports;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) {
        Reports newReport = Reports.builder()
                .reportId(1)
                .status(2)
                .build();
        System.out.println(newReport);

    }
}
