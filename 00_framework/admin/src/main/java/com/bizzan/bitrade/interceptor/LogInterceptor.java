package com.bizzan.bitrade.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.AdminAccessLog;
import com.bizzan.bitrade.service.AdminAccessLogService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
public class LogInterceptor implements HandlerInterceptor, AfterReturningAdvice {

    @Override
    public void afterReturning(Object arg0, Method arg1, Object[] arg2, Object arg3) {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        int index = request.getContextPath().length();
        String uri = request.getRequestURI().substring(index);
        String method = request.getMethod();
        String ip = remoteIp(request);
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AccessLog al = handlerMethod.getMethodAnnotation(AccessLog.class);
        if (al != null) {
            AdminModule module = al.module();
            String operation = al.operation();
            log.info("module={},operation={}", module, operation);
            int dotIndex = uri.lastIndexOf(".");
            String accessRuleUri = dotIndex > 0 ? uri.substring(0, dotIndex) : uri;
            Admin admin = (Admin) request.getSession().getAttribute(SysConstant.SESSION_ADMIN);
            long adminUid = admin == null ? -1 : admin.getId();
            AdminAccessLog adminAccessLog = new AdminAccessLog();
            adminAccessLog.setAdminId(adminUid);
            adminAccessLog.setAccessIp(ip);
            adminAccessLog.setAccessMethod(method);
            adminAccessLog.setOperation(operation);
            adminAccessLog.setModule(module);
            adminAccessLog.setUri(accessRuleUri);
            //解决service为null无法注入问题
            BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            AdminAccessLogService logService = (AdminAccessLogService) factory.getBean("adminAccessLogService");
            logService.saveLog(adminAccessLog);
        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub

    }

    public String remoteIp(HttpServletRequest request) {
        if (StringUtils.isNotBlank(request.getHeader("X-Real-IP"))) {
            return request.getHeader("X-Real-IP");
        } else if (StringUtils.isNotBlank(request.getHeader("X-Forwarded-For"))) {
            return request.getHeader("X-Forwarded-For");
        } else if (StringUtils.isNotBlank(request.getHeader("Proxy-Client-IP"))) {
            return request.getHeader("Proxy-Client-IP");
        }
        return request.getRemoteAddr();
    }
}
