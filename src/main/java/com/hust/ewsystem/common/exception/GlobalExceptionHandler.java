package com.hust.ewsystem.common.exception;


import com.hust.ewsystem.common.result.EwsResult;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public EwsResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        return EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "校验参数失败");
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public EwsResult<?> handleConstraintViolationException(ConstraintViolationException e) {
        e.printStackTrace();
        return EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "校验参数失败");
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public EwsResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
        e.printStackTrace();
        return EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "校验参数失败");
    }
    @ExceptionHandler(FileException.class)
    public EwsResult<?> handleFileSaveException(FileException e) {
        e.printStackTrace();
        return EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "文件保存失败");
    }
}
