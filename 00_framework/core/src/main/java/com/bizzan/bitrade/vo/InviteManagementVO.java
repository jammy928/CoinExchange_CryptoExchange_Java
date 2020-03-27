package com.bizzan.bitrade.vo;

import lombok.Data;

@Data
public class InviteManagementVO {
    private Long id;
    private String realName;
    private String mobilePhone;
    private String email;
    private Integer pageNo;
    private Integer pageNumber;
    private Integer pageSize;
}
