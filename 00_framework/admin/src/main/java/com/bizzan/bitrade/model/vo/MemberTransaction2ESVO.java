package com.bizzan.bitrade.model.vo;

import lombok.Data;

@Data
public class MemberTransaction2ESVO {

    Integer pageNo = 1;

    Integer pageSize = 10;

    private String startTime;

    private String endTime;


    private String minMoney ;

    private String maxMoney ;

    private String memberId ;
    private String minFee ;

    private String maxFee ;

    private String type ;
}
