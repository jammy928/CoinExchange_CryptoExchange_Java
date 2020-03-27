package com.bizzan.bitrade.vo;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

import com.bizzan.bitrade.annotation.Excel;

@Data
@Builder
public class PromotionMemberVO implements Serializable{

    @Excel(name="id")
    private long id ;
    @Excel(name="用户名")
    private String username ;
    @Excel(name="真名")
    private String realName ;
    @Excel(name="邮箱")
    private String email ;
    @Excel(name="手机号")
    private String mobilePhone ;
    @Excel(name="邀请码")
    private String promotionCode ;
    @Excel(name="邀请总数")
    private int promotionNum ;
    @Excel(name="邀请总奖励")
    private Map<String,String> reward ;

}
