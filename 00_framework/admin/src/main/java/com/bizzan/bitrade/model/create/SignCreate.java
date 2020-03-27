package com.bizzan.bitrade.model.create;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;

import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Sign;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.SignService;
import com.bizzan.bitrade.util.DateUtil;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Shaoxianjun
 * @Description:
 * @date 2019/5/311:24
 */
@Data
public class SignCreate {

    /**
     * 赠送币种
     */
    @NotBlank(message = "unit can not be empty!")
    private String unit;

    /**
     * 赠送数目
     */
    @Min(value = 0, message = "amount must gt 0!")
    private BigDecimal amount;

    /**
     * 结束日期
     */
    @NotNull(message = "endDate can not be null!")
    private Date endDate;

    public Sign transformation(SignService signService, CoinService coinService) {
        //校验是否签到进行中
        Sign underway = signService.fetchUnderway();
        Assert.isNull(underway, "validate underway!");

        //校验币种存在
        Coin coin = coinService.findByUnit(unit);
        Assert.notNull(coin, "validate unit!");

        //校验时间
        DateUtil.validateEndDate(endDate);

        //返回对象
        Sign sign = new Sign();
        sign.setAmount(amount);
        sign.setCoin(coin);
        sign.setEndDate(endDate);

        return sign;
    }
}
