package com.hust.ewsystem.common.exception;


import com.hust.ewsystem.common.result.EwsResult;
import com.sun.xml.internal.ws.handler.HandlerException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public EwsResult<?> HandlerException(Exception e){
        e.printStackTrace();
        String message = e.getMessage();
        if(message != null) return EwsResult.error(message);
        return EwsResult.error("操作失败");
    }
}
