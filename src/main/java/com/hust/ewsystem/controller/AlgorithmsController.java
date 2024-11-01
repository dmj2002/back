package com.hust.ewsystem.controller;


import com.hust.ewsystem.common.exception.FileException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.entity.AlgForm;
import com.hust.ewsystem.entity.Algorithms;
import com.hust.ewsystem.service.AlgorithmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/algorithms")
public class AlgorithmsController {

    @Autowired
    private AlgorithmsService algorithmsService;

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;

    @PostMapping("/add")
    @Transactional
    public EwsResult<?> addAlgorithm(@ModelAttribute @Validated AlgForm algForm) {
        Algorithms algorithms = algForm.getAlgorithms();
        MultipartFile trainFile = algForm.getTrain();
        MultipartFile predictFile = algForm.getPredict();
        // 额外检查文件是否为空
        if (trainFile.isEmpty()) {
            throw new FileException("训练文件不能为空");
        }
        if (predictFile.isEmpty()) {
            throw new FileException("预测文件不能为空");
        }
        boolean save = algorithmsService.save(algorithms);
        if(save) {
            Integer algorithmId = algorithms.getAlgorithmId();
            // 定义文件保存路径
            String algorithmDir = pythonFilePath + "/A" + String.format("%04d", algorithmId);
            File dir = new File(algorithmDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 创建目录
            }
            // 保存文件
            try {
                trainFile.transferTo(new File(dir, "train.py"));
                predictFile.transferTo(new File(dir, "predict.py"));
            } catch (IOException e) {
                throw new FileException("文件保存失败: " + e.getMessage());
            }
            Map<String, Object> result = new HashMap<>();
            result.put("algorithmId", algorithmId);
            return EwsResult.OK("新建算法成功", result);
        }
        return EwsResult.error("新建算法失败");
    }
    @DeleteMapping("/delete/{algorithmId}")
    public EwsResult<?> deleteAlgoithm(@PathVariable Integer algorithmId) {
        boolean remove = algorithmsService.removeById(algorithmId);
        if(remove) {
            String algorithmDir = pythonFilePath + "/A" + String.format("%04d", algorithmId);
            File dir = new File(algorithmDir);
            if (dir.exists()) {
                dir.delete(); // 删除目录
            }
            return EwsResult.OK("删除算法成功");
        }
        return EwsResult.error("删除算法失败");
    }
    @PutMapping("/update")
    public EwsResult<?> updateAlgorithm(@RequestBody @Validated Algorithms algorithms) {
        boolean update = algorithmsService.updateById(algorithms);
        if(update) {
            return EwsResult.OK("更新算法成功");
        }
        return EwsResult.error("更新算法失败");
    }
    @GetMapping("/get/{algorithmId}")
    public EwsResult<?> getAlgorithm(@PathVariable Integer algorithmId) {
        Algorithms algorithm = algorithmsService.getById(algorithmId);
        return Objects.isNull(algorithm) ? EwsResult.error("未找到该算法") : EwsResult.OK(algorithm);
    }
    @GetMapping("/list")
    public EwsResult<List<Algorithms>> listAlgorithm() {
        List<Algorithms> algorithm = algorithmsService.list();
        return EwsResult.OK(algorithm);
    }
}
