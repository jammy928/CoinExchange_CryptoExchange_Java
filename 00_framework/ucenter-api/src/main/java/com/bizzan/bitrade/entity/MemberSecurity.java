package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author GS
 * @date 2018年01月15日
 */
@Builder
@Data
public class MemberSecurity {
    private String username;
    private long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private BooleanEnum realVerified;
    private BooleanEnum emailVerified;
    private BooleanEnum phoneVerified;
    private BooleanEnum loginVerified;
    private BooleanEnum fundsVerified;
    private BooleanEnum realAuditing;
    private String mobilePhone;
    private String email;
    private String realName;
    private String realNameRejectReason;
    private String idCard;
    private String avatar;
    private BooleanEnum accountVerified;
    private Integer googleStatus;
}
