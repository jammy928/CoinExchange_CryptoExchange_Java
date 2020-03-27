package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PromotionRewardType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 推广奖励设置
 *
 * @author GS
 * @date 2018年03月08日
 */
@Data
@Entity
public class RewardPromotionSetting {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 如果是币币交易推广不用设置币种
     */
    @JoinColumn(name = "coin_id", nullable = true)
    @ManyToOne
    private Coin coin;
    /**
     * 启用禁用
     */
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum status = BooleanEnum.IS_FALSE;
    @Enumerated(EnumType.ORDINAL)
    private PromotionRewardType type;
    /**
     * 推广注册：{"one": 1,"two": 0.1}
     * <p>
     * 法币暂定首次推广交易：{"one":  10,"two": 5}交易数量占比,交易数量全部换算成usdt来计算
     * <p>
     * 币币推广交易：{"one":  10,"two": 5}手续费占比
     */
    private String info;

    /**
     * 生效时间，从注册之日算起。单位天 .主要推广交易用到
     */
    private int effectiveTime = 0;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 最近更改者
     */
    @JoinColumn(name = "admin_id")
    @ManyToOne
    private Admin admin;

    @Max(value = 100)
    @Min(value = 0)
    @Transient
    private BigDecimal one ;

    @Max(value = 100)
    @Min(value = 0)
    @Transient
    private BigDecimal two ;

}
