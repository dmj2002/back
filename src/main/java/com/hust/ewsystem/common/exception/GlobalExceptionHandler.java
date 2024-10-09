package com.hust.ewsystem.common.exception;


import com.hust.ewsystem.common.result.EwsResult;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public EwsResult<?> HandlerException(Exception e){
        e.printStackTrace();
        return EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败");
    }
}
