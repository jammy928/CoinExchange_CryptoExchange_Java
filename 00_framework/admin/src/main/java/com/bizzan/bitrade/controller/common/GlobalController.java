package com.bizzan.bitrade.controller.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalController {

    @ExceptionHandler({Exception.class})
    public String exception(Exception e) {
        log.info(e.getMessage());
        e.printStackTrace();
        return "exception";
    }
}
