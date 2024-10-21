package com.hust.ewsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ModelsControllerTest {

    @Autowired
    private RestTemplate restTemplate;
    @Test
    void trainModel() {
        String url = "http://localhost:8000/api/training/";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("train_point", new String[]{"WindSpeed", "GridPower"});
        requestBody.put("test_point", new String[]{"WTState", "T_GBS_In_Visu", "T_GBS_Out_Visu", "gConvCabinetTemp", "T_GW_U1_Visu", "T_GW_V1_Visu", "T_GW_W1_Visu", "WindSpeed", "GridPower"});
//        Map<String, Object> jsonResp =  restTemplate.postForObject(url, requestBody,Map.class);
        String jsonResp = restTemplate.postForObject(url, requestBody, String.class);
        // 使用 ObjectMapper 将 JSON 字符串解析为 Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            responseMap = objectMapper.readValue(jsonResp, Map.class);
            // 处理和打印返回的 Map
            System.out.println(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jsonResp);
    }
}