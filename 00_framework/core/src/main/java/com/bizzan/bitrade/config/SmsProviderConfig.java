package com.bizzan.bitrade.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.bizzan.bitrade.vendor.provider.support.ChuangRuiSMSProvider;
import com.bizzan.bitrade.vendor.provider.support.DiyiSMSProvider;
import com.bizzan.bitrade.vendor.provider.support.EmaySMSProvider;
import com.bizzan.bitrade.vendor.provider.support.HuaXinSMSProvider;

@Configuration
public class SmsProviderConfig {

    @Value("${sms.gateway:}")
    private String gateway;
    @Value("${sms.username:}")
    private String username;
    @Value("${sms.password:}")
    private String password;
    @Value("${sms.sign:}")
    private String sign;
    @Value("${sms.internationalGateway:}")
    private String internationalGateway;
    @Value("${sms.internationalUsername:}")
    private String internationalUsername;
    @Value("${sms.internationalPassword:}")
    private String internationalPassword;
    @Value("${access.key.id:}")
    private String accessKey;
    @Value("${access.key.secret:}")
    private String accessSecret;


    @Bean
    public SMSProvider getSMSProvider(@Value("${sms.driver:}") String driverName) {
    	//默认发送：第一信息
    	return new DiyiSMSProvider(username, password, sign);
//        return new ChuangRuiSMSProvider(gateway, username, password, sign,accessKey,accessSecret);
//        if (StringUtils.isEmpty(driverName)) {
//            return new ChuangRuiSMSProvider(gateway, username, password, sign,accessKey,accessSecret);
//        }
//        if (driverName.equalsIgnoreCase(ChuangRuiSMSProvider.getName())) {
//            return new ChuangRuiSMSProvider(gateway, username, password, sign,accessKey,accessSecret);
//        } else if (driverName.equalsIgnoreCase(EmaySMSProvider.getName())) {
//            return new EmaySMSProvider(gateway, username, password);
//        }else if (driverName.equalsIgnoreCase(HuaXinSMSProvider.getName())) {
//            return new HuaXinSMSProvider(gateway, username, password,internationalGateway,internationalUsername,internationalPassword,sign);
//        }  else {
//            return null;
//        }
    }
}
