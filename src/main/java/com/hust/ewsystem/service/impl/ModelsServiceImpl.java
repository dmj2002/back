package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.entity.Models;
import com.hust.ewsystem.mapper.ModelsMapper;
import com.hust.ewsystem.service.ModelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@Transactional
public class ModelsServiceImpl extends ServiceImpl<ModelsMapper, Models> implements ModelsService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map<String, Object> trainModel(Models model) {
        Map<String, Object> objectMap = sendTrainingRequest(model);
        return objectMap;
    }

    private Map<String, Object> sendTrainingRequest(Models model) {
        String url = "http://localhost:8000/api/training";
        String jsonResp = sendRequest(url, model);
        return parseJson(jsonResp);
    }

    private Map<String, Object> parseJson(String jsonResp) {
        
        return null;
    }

    private String sendRequest(String url, Object requestbody) {
        String resp = null;
        try {
            //only获取响应体
            resp = restTemplate.postForObject(url, requestbody, String.class);
        } catch (Exception e) {
            //日志处理
            e.printStackTrace();
            return null; // 返回 null 或者可以根据需要返回错误信息
        }
        return resp; // 返回响应内容
    }
}
