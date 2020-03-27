package com.bizzan.bitrade.dto;

import lombok.Data;

@Data
public class BaseMemberDTO {

    private Long memberId ;

    private String realName ;

    private String email ;

    private String mobilePhone ;

    private String username ;
}
