package com.bizzan.bitrade.vo;

import lombok.Data;

@Data
public class MemberInviteStasticVO {
    private Long id;
    private String mobilePhone;
    private Integer rankType;
    private Integer pageNo;
    private Integer pageNumber;
    private Integer pageSize;
}
