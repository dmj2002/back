package com.hust.ewsystem.common.exception;


import com.hust.ewsystem.common.result.EwsResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<EwsResult<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        EwsResult<?> result = EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "校验参数失败");
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<EwsResult<?>> handleConstraintViolationException(ConstraintViolationException e) {
        e.printStackTrace();
        EwsResult<?> result = EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "校验参数失败");
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
    }
    @ExceptionHandler(FileException.class)
    public ResponseEntity<EwsResult<?>> handleFileSaveException(FileException e) {
        e.printStackTrace();
        EwsResult<?> result = EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "文件保存失败");
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
    }
    @ExceptionHandler(CrudException.class)
    public ResponseEntity<EwsResult<?>> handleFileSaveException(CrudException e) {
        e.printStackTrace();
        EwsResult<?> result = EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "数据库操作失败");
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<EwsResult<?>> handleMissingParams(MissingServletRequestParameterException e) {
        e.printStackTrace();
        EwsResult<?> result = EwsResult.error("缺少必需的参数: " +  e.getParameterName());
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
    }
}
