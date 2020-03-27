package com.bizzan.bitrade.core;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bizzan.bitrade.util.MessageResult;

/**
 * @author Shaoxianjun
 * @date 2018年12月22日
 */
@ControllerAdvice
public class AdminMyControllerAdvice {
    /**
     * 拦截捕捉无权限异常
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = AuthorizationException.class)
    public MessageResult handleAuthorizationError(AuthorizationException ex) {
        ex.printStackTrace();
        MessageResult result = MessageResult.error(5000, "unauthorized");
        return result;
    }

    @ResponseBody
    @ExceptionHandler({AuthenticationException.class,UnauthenticatedException.class})
    public MessageResult handleAuthenticationError(AuthorizationException ex) {
        ex.printStackTrace();
        MessageResult result = MessageResult.error(4000, "please login");
        return result;
    }
}
