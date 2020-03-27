package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="exchange_favor_symbol")
public class FavorSymbol {
    @Id
    @GeneratedValue
    private Long id;
    private String symbol;
    private Long memberId;
    private String addTime;
}
