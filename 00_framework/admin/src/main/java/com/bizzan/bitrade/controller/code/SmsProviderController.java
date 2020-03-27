package com.bizzan.bitrade.controller.code;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;

import static com.bizzan.bitrade.util.MessageResult.error;
import static com.bizzan.bitrade.util.MessageResult.success;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/code/sms-provider")
public class SmsProviderController {

    private Logger logger = LoggerFactory.getLogger(SmsProviderController.class);

    @Autowired
    private SMSProvider smsProvider;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LocaleMessageSourceService msService;

    /**
     * 币种管理 修改币种信息手机验证码
     *
     * @param admin
     * @return
     */
    @RequestMapping(value = "/system/coin-revise", method = RequestMethod.POST)
    public MessageResult sendReviseCode(@SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        Assert.notNull(admin, msService.getMessage("DATA_EXPIRED_LOGIN_AGAIN"));
        return sendCode(admin.getMobilePhone(), SysConstant.ADMIN_COIN_REVISE_PHONE_PREFIX);
    }

    /**
     * 币币管理 币币设置 手机验证码
     *
     * @param admin
     * @return
     */
    @RequestMapping("/exchange-coin-set")
    public MessageResult sendExchangeCoinSet(@SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        Assert.notNull(admin, msService.getMessage("DATA_EXPIRED_LOGIN_AGAIN"));
        return sendCode(admin.getMobilePhone(), SysConstant.ADMIN_EXCHANGE_COIN_SET_PREFIX);
    }

    /**
     * 转入冷钱包 手机验证码
     *
     * @param admin
     * @return
     */
    @RequestMapping("/transfer-cold-wallet")
    public MessageResult sendTransfer(@SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
        Assert.notNull(admin, msService.getMessage("DATA_EXPIRED_LOGIN_AGAIN"));
        return sendCode(admin.getMobilePhone(), SysConstant.ADMIN_COIN_TRANSFER_COLD_PREFIX);
    }

    /**
     * 后台登录  手机验证码
     *
     * @param phone
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public MessageResult send(String phone) {
        return sendCode(phone, SysConstant.ADMIN_LOGIN_PHONE_PREFIX);
    }


    private MessageResult sendCode(String phone, String prefix) {
        Assert.notNull(phone, msService.getMessage("NO_CELL_PHONE_NUMBER"));
        MessageResult result;
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        try {
            ValueOperations valueOperations = redisTemplate.opsForValue();
            String key = prefix + phone;
            long expire = valueOperations.getOperations().getExpire(key, TimeUnit.SECONDS);
            if (expire < 600 && expire > 540) {
                return error(msService.getMessage("SEND_CODE_FAILURE_ONE"));
            }
            result = smsProvider.sendVerifyMessage(phone, randomCode);
            if (result.getCode() == 0) {
                logger.info("短信验证码:{}", randomCode);
                valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
                return success(msService.getMessage("SEND_CODE_SUCCESS") + phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error(msService.getMessage("REQUEST_FAILED"));
    }
}
