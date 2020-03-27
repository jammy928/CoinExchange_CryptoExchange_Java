package com.bizzan.bitrade.model.update;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;

import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Sign;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.util.DateUtil;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Shaoxianjun
 * @Title: ${file_name}
 * @Description:
 * @date 2019/5/314:21
 */
@Data
public class SignUpdate {
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

    /**
     * @param coinService
     * @param sign        持久态
     * @return
     */
    public Sign transformation(CoinService coinService, Sign sign) {
        Coin coin = coinService.findByUnit(unit);
        Assert.notNull(coin, "validate unit!");
        //校验时间
        DateUtil.validateEndDate(endDate);
        sign.setCoin(coin);
        sign.setEndDate(endDate);
        sign.setAmount(amount);
        return sign;
    }

}
