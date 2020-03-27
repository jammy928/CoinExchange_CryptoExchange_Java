package com.bizzan.bitrade.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.bizzan.bitrade.core.AdminRealm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Shaoxianjun
 * @date 2018年12月19日
 */
@Slf4j
@Configuration
public class ShiroConfig {

    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     *
     * @param securityManager
     * @return
     */

    @Bean(name="shiroFilter")
    @DependsOn({"securityManager"})
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        log.info("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/captcha", "anon");
        filterChainDefinitionMap.put("/admin/code/**", "anon");
        filterChainDefinitionMap.put("admin/**/page-query", "user");
        filterChainDefinitionMap.put("/admin/employee/logout", "logout");
        filterChainDefinitionMap.put("admin/**/detail", "authc");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        /*shiroFilterFactoryBean.setU("/403");*/
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    /**
     * Shiro生命周期处理器
     *
     *   /*1.LifecycleBeanPostProcessor，这是个DestructionAwareBeanPostProcessor的子类，
     *   负责org.apache.shiro.util.Initializable类型bean的生命周期的，初始化和销毁。
     *   主要是AuthorizingRealm类的子类，以及EhCacheManager类。
     *
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        LifecycleBeanPostProcessor lifecycleBeanPostProcessor = new LifecycleBeanPostProcessor();
        return lifecycleBeanPostProcessor;
    }


    /**
     * shiro缓存管理器;
     * 需要注入对应的其它的实体类中：
     * 安全管理器：securityManager
     *
     * @return
     */
    @Bean(name="ehCacheManager")
    @DependsOn("lifecycleBeanPostProcessor")
    public EhCacheManager ehCacheManager() {
        log.info("ShiroConfiguration.getEhCacheManager()");
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return cacheManager;
    }

    @Bean(name="adminRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public AdminRealm adminRealm(EhCacheManager ehCacheManager) {
        AdminRealm adminRealm = new AdminRealm() ;
        //为确保密码安全，可以定义hash算法，（此处未做任何hash，直接用密码匹配）
        /*HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("SHA-1");
        matcher.setHashIterations(2);
        matcher.setStoredCredentialsHexEncoded(true);
        adminRealm.setCredentialsMatcher(matcher);*/
        adminRealm.setCacheManager(ehCacheManager);
        return adminRealm;
    }

    /**
     * 设置rememberMe  Cookie 7天
     * @return
     */
    @Bean(name="simpleCookie")
    public SimpleCookie getSimpleCookie(){
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("rememberMe");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(7*24*60*60);
        return simpleCookie ;
    }

    /**
     * cookie 管理器
     * @return
     */
    @Bean(name="cookieRememberMeManager")
    @DependsOn({"simpleCookie"})
    public CookieRememberMeManager getCookieRememberMeManager(SimpleCookie simpleCookie){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(simpleCookie);
        /**
         * 设置 rememberMe cookie 的密钥 ，不设置 很可能：javax.crypto.BadPaddingException: Given final block not properly padded
         */
        cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
        return cookieRememberMeManager ;
    }

    /**
     * @DependOn  :在初始化 defaultWebSecurityManager 实例前 强制先初始化 adminRealm ，ehCacheManager。。。。。
     * @param realm
     * @param ehCacheManager
     * @param cookieRememberMeManager
     * @return
     */

    @Bean(name = "securityManager")
    @DependsOn({"adminRealm","ehCacheManager","cookieRememberMeManager"})
    public DefaultWebSecurityManager getDefaultWebSecurityManager(AdminRealm realm, EhCacheManager ehCacheManager,CookieRememberMeManager cookieRememberMeManager) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        //设置realm.
        defaultWebSecurityManager.setRealm(realm);
        defaultWebSecurityManager.setCacheManager(ehCacheManager);
        defaultWebSecurityManager.setRememberMeManager(cookieRememberMeManager);
        return defaultWebSecurityManager;
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),
     * 需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证 * 配置以下两个bean
     * (DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能 * @return
     */

    /**
     * 由Advisor决定对哪些类的方法进行AOP代理。
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

    @Bean
    @DependsOn("securityManager")
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
