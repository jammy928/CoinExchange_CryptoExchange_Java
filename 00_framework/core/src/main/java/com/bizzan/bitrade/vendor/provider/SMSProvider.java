package com.bizzan.bitrade.vendor.provider;


import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;

import com.bizzan.bitrade.util.MessageResult;

import java.io.IOException;

public interface SMSProvider {
    /**
     * 发送单条短信
     *
     * @param mobile  手机号
     * @param content 短信内容
     * @return
     * @throws Exception
     */
    MessageResult sendSingleMessage(String mobile, String content) throws Exception;

    /**
     * 发送单条短信
     *
     * @param mobile  手机号
     * @param content 短信内容
     * @return
     * @throws Exception
     */
    MessageResult sendMessageByTempId(String mobile, String content,String templateId) throws Exception;

    /**
     * 发送自定义短信
     * @param mobile
     * @param content
     * @return
     * @throws Exception
     */
    MessageResult sendCustomMessage(String mobile, String content) throws Exception;
    
    /**
     * 发送验证码短信
     *
     * @param mobile     手机号
     * @param verifyCode 验证码
     * @return
     * @throws Exception
     */
    default MessageResult sendVerifyMessage(String mobile, String verifyCode) throws Exception {
        return sendSingleMessage(mobile, formatVerifyCode(verifyCode));
    }

    /**
     * 获取验证码信息格式
     *
     * @param code
     * @return
     */
    default String formatVerifyCode(String code) {
        return String.format("%s", code);
    }

    /**
     * 发送国际短信
     *
     * @param content
     * @param phone
     * @return
     */
    default MessageResult sendInternationalMessage(String content, String phone) throws IOException, DocumentException {
        return null;
    }
}
