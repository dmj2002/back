package com.hust.ewsystem;

import com.alibaba.fastjson.JSONObject;
import com.hust.ewsystem.common.exception.FileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class test {

    private static final Logger LOGGER = LoggerFactory.getLogger(test.class);
    public static void main(String[] args) {
        try {
            LOGGER.info("This is an info message.");
            LOGGER.debug("This is a debug message.");
            LOGGER.warn("This is a warning message.");
            throw new Exception("An example exception.");
        } catch (Exception e) {
            LOGGER.error("An error occurred: ", e);
        }
    }
}
