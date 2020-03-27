package com.bizzan.bitrade.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bizzan.bitrade.exception.GeeTestException;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.system.GeetestLib;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 极验验证
 *
 * @author GS
 * @date 2018年03月14日
 */
@Aspect
@Component
@Slf4j
public class GeeTestAspect {
    @Autowired
    private GeetestLib gtSdk;
    @Autowired
    private LocaleMessageSourceService msService;

    private ThreadLocal<Long> startTime = new ThreadLocal<>();
    
 // "||execution(public * com.bizzan.bitrade.controller.SmsController.resetPasswordCode(..))"+
    @Pointcut("execution(public * com.bizzan.bitrade.controller.RegisterController.registerByEmail(..))"+
            "||execution(public * com.bizzan.bitrade.controller.RegisterController.sendResetPasswordCode(..))")
    public void geeTest() {
    	
    }
    @Pointcut("execution(public * com.bizzan.bitrade.controller.SmsController.sendCheckCode(..))")
    public void webControllerAspect(){

    }
    @Before("webControllerAspect()")
    public void doWebBefore(JoinPoint joinPoint){
        log.info(">>>>>>>>>>>>>>>>>>注册发送验证码>>>>>>>>>>");
        sendVerifyCode(joinPoint);
    }

    private void sendVerifyCode(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Real-IP");
        // 记录下请求内容
        log.info("请求路径 : " + request.getRequestURL().toString());
        log.info("请求方式 : " + request.getMethod());
        log.info("请求IP  : " + ip);
        log.info("请求方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("请求参数 : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "webControllerAspect()")
    public void doWebAfterReturning()  {
        log.info("处理耗时：" + (System.currentTimeMillis() - startTime.get()) + "ms");
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<验证码发送成功<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Before("geeTest()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        log.info("❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤");
        gee(joinPoint);
    }



    public void gee(JoinPoint joinPoint) throws GeeTestException {
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Real-IP");
        String challenge = request.getParameter(GeetestLib.fn_geetest_challenge);
        String validate = request.getParameter(GeetestLib.fn_geetest_validate);
        String seccode = request.getParameter(GeetestLib.fn_geetest_seccode);
        log.info(">>>>>>>>>>>>challenge>>>>"+challenge);
        log.info(">>>>>>>>>>>>validate>>>>"+validate);
        log.info(">>>>>>>>>>>>seccode>>>>"+seccode);
        log.info(">>>>>>>>"+ip+">>>>>>>>>");
        //从session中获取gt-server状态
        Integer gt_server_status_code=(Integer) request.getSession().getAttribute(gtSdk.gtServerStatusSessionKey);
        //从session中获取userid
        String userid = (String)request.getSession().getAttribute("userid");
        //自定义参数,可选择添加
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("user_id", userid); //网站用户id
        //param.put("client_type", "web"); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("ip_address", ip); //传输用户请求验证时所携带的IP

        int gtResult = 0;
        log.info(">>>>>>>>>>>>从session中获取get-server状态>>>>>"+gt_server_status_code);
        if (null != gt_server_status_code && gt_server_status_code == 1) {
            //gt-server正常，向gt-server进行二次验证
            gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
        } else {
            // gt-server非正常情况下，进行failback模式验证
            System.out.println("failback:use your own server captcha validate");
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
        }
        log.info(">>>>>>>>>>>>从session中获取gtResult状态>>>>>"+gtResult);
        if (gtResult!=1){
            throw new GeeTestException(msService.getMessage("GEETEST_FAIL"));
        }
        // 记录下请求内容
        log.info("请求路径 : " + request.getRequestURL().toString());
        log.info("请求方式 : " + request.getMethod());
        log.info("请求IP  : " + ip);
        log.info("请求方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("请求参数 : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "geeTest()")
    public void doAfterReturning() {
        log.info("处理耗时：" + (System.currentTimeMillis() - startTime.get()) + "ms");
        log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        startTime.remove();
    }
}
