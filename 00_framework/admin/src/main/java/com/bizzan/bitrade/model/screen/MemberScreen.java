package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.bizzan.bitrade.constant.CommonStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.util.Date;

@Data
public class MemberScreen extends AccountScreen{

    /**
     * 会员注册时间
     */

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private Date endTime;

    /**
     * 认证商家状态：(012 未认证/审核中/已认证)
     */
    private CertifiedBusinessStatus status;
    /**
     * 01(正常/非法)
     */
    private CommonStatus commonStatus ;
}
