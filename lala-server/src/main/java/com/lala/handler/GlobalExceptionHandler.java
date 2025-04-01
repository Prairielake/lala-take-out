package com.lala.handler;

import com.lala.constant.MessageConstant;
import com.lala.exception.BaseException;
import com.lala.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /*
    * 处理sql异常
    * */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")){
//            String[] split = message.split(" ");
//            String username = split[2];
            return Result.error(MessageConstant.ALREADY_EXISTS);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
