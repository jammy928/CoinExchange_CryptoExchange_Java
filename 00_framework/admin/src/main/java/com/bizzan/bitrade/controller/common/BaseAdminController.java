package com.bizzan.bitrade.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.MessageResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @author Shaoxianjun
 * @date 2019年12月19日
 */
@Component
public class BaseAdminController extends BaseController {

    @Autowired
    private RedisTemplate redisTemplate ;
    @Autowired
    private LocaleMessageSourceService msService;

    protected Admin getAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Admin) session.getAttribute(SysConstant.SESSION_ADMIN);
    }

    /**
     * 判断手机验证码正确否
     * @param code 验证码
     * @param key redis中的key 前缀+手机号
     * @return
     */
    protected MessageResult checkCode(String code, String key){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object value = valueOperations.get(key);
        if(value==null) {
            return error(msService.getMessage("CODE_NOT_EXIST_RESEND"));
        }
        if(!value.toString().equals(code)) {
            return  error(msService.getMessage("CODE_ERROR"));
        }
        valueOperations.getOperations().delete(key);
        /**
         * 十分钟之内无需再次验证
         */
        valueOperations.set(key+"_PASS",true,10, TimeUnit.MINUTES);
        return success(msService.getMessage("CODE_CORRECT"));
    }
}
