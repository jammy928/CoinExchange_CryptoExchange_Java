package com.bizzan.bitrade.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class InitPlate {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;



    //交易对
    private String symbol;
    //拉升初始价
    private String finalPrice;
    //拉升最终价
    private String initPrice;
    //拉升时长
    private String relativeTime ;

    //干扰因子 0-70
    private String interferenceFactor;



}
