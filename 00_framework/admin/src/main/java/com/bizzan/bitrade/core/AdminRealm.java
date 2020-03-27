package com.bizzan.bitrade.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.SysPermission;
import com.bizzan.bitrade.entity.SysRole;
import com.bizzan.bitrade.service.AdminService;
import com.bizzan.bitrade.service.SysPermissionService;
import com.bizzan.bitrade.service.SysRoleService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Shaoxianjun
 * @date 2018年12月18日
 */
@Slf4j
@Component(value = "realm")
public class AdminRealm extends AuthorizingRealm {

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private AdminService adminService;
    @Resource
    private SysPermissionService sysPermissionService;

    /**
     * 授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String currentUsername = (String) getAvailablePrincipal(principals);
        log.info("doGetAuthorizationInfo,user:" + currentUsername);
        List<String> permissionList = new ArrayList<>();
        Admin admin = (Admin) getSession(SysConstant.SESSION_ADMIN);
        if (null == admin) {
            throw new AuthorizationException();
        }
        try {
            List<SysPermission> list;
            if ("root".equalsIgnoreCase(admin.getUsername())) {
                list = sysPermissionService.findAll();
            } else {
                SysRole sysRole = sysRoleService.findOne(admin.getRoleId());
                list = sysRole.getPermissions();
            }
            //获取当前用户权限列表
            list.forEach(x -> {
                if (!StringUtils.isEmpty(x.getName())) {
                    permissionList.add(x.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthorizationException();
        }
        SimpleAuthorizationInfo simpleAuthorInfo = new SimpleAuthorizationInfo();
        simpleAuthorInfo.addStringPermissions(permissionList);
        return simpleAuthorInfo;
    }


    /**
     * 认证
     *
     * @param authToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        //获取基于用户名和密码的令牌
        UsernamePasswordToken token = (UsernamePasswordToken) authToken;
        String password = String.valueOf(token.getPassword());
        String username = token.getUsername();

        Admin admin = null;

        AuthenticationInfo authInfo = new SimpleAuthenticationInfo(token.getUsername(), token.getCredentials(),this.getName());
        try {
            admin = adminService.login(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (admin == null) {
            throw new AuthenticationException("用户名或密码错误");
        } else if (admin.getEnable() == CommonStatus.ILLEGAL) {
            throw new AuthenticationException("该用户已被禁用");
        } else {
            //添加登录日志
            adminService.update(new Date(), token.getHost(), admin.getId());
            setSession(SysConstant.SESSION_ADMIN, admin);
            return authInfo;
        }
    }

    /**
     * 将一些数据放到ShiroSession中,以便于其它地方使用
     * 比如Controller,使用时直接用HttpSession.getAttribute(key)就可以取到
     *
     * @param key
     * @param value
     */
    private void setSession(Object key, Object value) {
        Subject currentUser = SecurityUtils.getSubject();
        if (null != currentUser) {
            Session session = currentUser.getSession();
            session.setTimeout(1800000L);
            log.info("Session默认超时时间为[" + session.getTimeout() + "]毫秒");
            if (null != session) {
                session.setAttribute(key, value);
            }
        }
    }

    private Object getSession(String key) {
        Subject currentUser = SecurityUtils.getSubject();
        if (null != currentUser) {
            return currentUser.getSession().getAttribute(key);
        }
        return null;
    }
}
