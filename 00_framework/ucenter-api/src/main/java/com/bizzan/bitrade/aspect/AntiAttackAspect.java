package com.bizzan.bitrade.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.service.LocaleMessageSourceService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 登录之后发送邮件或者短信频率最快也只能一分钟一次
 *
 * @author GS
 * @date 2018年04月03日
 */
@Aspect
@Component
@Slf4j
public class AntiAttackAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private LocaleMessageSourceService localeMessageSourceService;

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * com.bizzan.bitrade.controller.RegisterController.sendBindEmail(..))" +
            "||execution(public * com.bizzan.bitrade.controller.RegisterController.sendAddAddress(..))" +
            "||execution(public * com.bizzan.bitrade.controller.SmsController.sendResetTransactionCode(..))" +
            "||execution(public * com.bizzan.bitrade.controller.SmsController.setBindPhoneCode(..))" +
            "||execution(public * com.bizzan.bitrade.controller.SmsController.updatePasswordCode(..))" +
            "||execution(public * com.bizzan.bitrade.controller.SmsController.addAddressCode(..))" +
            "||execution(public * com.bizzan.bitrade.controller.SmsController.resetPhoneCode(..))")
    public void antiAttack() {
    }

    @Before("antiAttack()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        log.info("❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤");
        check(joinPoint);
    }

    public void check(JoinPoint joinPoint) throws Exception {
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = SysConstant.ANTI_ATTACK_ + request.getSession().getId();
        Object code = valueOperations.get(key);
        if (code != null) {
            throw new IllegalArgumentException(localeMessageSourceService.getMessage("FREQUENTLY_REQUEST"));
        }
    }

    @AfterReturning(pointcut = "antiAttack()")
    public void doAfterReturning() throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String key = SysConstant.ANTI_ATTACK_ + request.getSession().getId();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, "send sms all too often", 1, TimeUnit.MINUTES);
        log.info("处理耗时：" + (System.currentTimeMillis() - startTime.get()) + "ms");
        log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        startTime.remove();
    }
}
