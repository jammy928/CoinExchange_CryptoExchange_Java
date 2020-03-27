package com.bizzan.bitrade.ext;

import org.apache.commons.lang.StringUtils;
import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SmartHttpSessionStrategy implements HttpSessionStrategy {
    private CookieHttpSessionStrategy browser;
    private HeaderHttpSessionStrategy api;
    private String tokenName = "x-auth-token";

    public SmartHttpSessionStrategy(CookieHttpSessionStrategy browser, HeaderHttpSessionStrategy api) {
        this.browser = browser;
        this.api = api;
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        String paramToken = request.getParameter(tokenName);
        if (StringUtils.isNotEmpty(paramToken)) {
            return paramToken;
        }
        return getStrategy(request).getRequestedSessionId(request);
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {
        getStrategy(request).onNewSession(session, request, response);
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
        getStrategy(request).onInvalidateSession(request, response);
    }

    private HttpSessionStrategy getStrategy(HttpServletRequest request) {
        String authType = request.getHeader("x-auth-token");
        if (authType == null) {
            return this.browser;
        } else {
            return this.api;
        }
    }
}