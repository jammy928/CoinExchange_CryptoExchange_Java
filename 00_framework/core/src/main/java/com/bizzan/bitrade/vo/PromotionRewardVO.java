package com.bizzan.bitrade.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PromotionRewardVO {

    private String username ;

    private String realName ;

    private String mobilePhone ;

    private String email ;

    private String promotionCode ;

    private int promotionNum ;

    private BigDecimal promotionReward ;
}
