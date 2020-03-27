package com.bizzan.bitrade.vo;

import com.bizzan.bitrade.constant.PromotionLevel;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberPromotion;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 注册推广奖励明细
 */
@Data
public class RegisterPromotionVO {

   /**
     * 被推荐者
     */
    private BigInteger id ;

    private String presentee ;

    private String presenteeRealName;

    private String presenteeEmail;

    private String presenteePhone;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date promotionTime;

   /**
    * 一级
    *
   ONE("一级"),
   *
    * 二级
    *
   TWO("二级")
   *
    * 三级
    *
   THREE("三级");
    */
    private int promotionLevel;

    private BigDecimal reward ;

    private String unit ;

}
