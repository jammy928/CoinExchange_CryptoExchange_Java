package com.bizzan.bitrade.entity;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table
public class ExchangeOrder implements Serializable {
    @Id
    private String orderId;
    private Long memberId;
    //挂单类型
    private ExchangeOrderType type;
    //买入或卖出量，对于市价买入单表
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 ")
    private BigDecimal amount = BigDecimal.ZERO;
    //交易对符号
    private String symbol;
    //成交量
    @Column(columnDefinition = "decimal(26,16) DEFAULT 0 ")
    private BigDecimal tradedAmount = BigDecimal.ZERO;
    //成交额，对市价买单有用
    @Column(columnDefinition = "decimal(26,16) DEFAULT 0 ")
    private BigDecimal turnover = BigDecimal.ZERO;
    //币单位
    private String coinSymbol;
    //结算单位
    private String baseSymbol;
    //订单状态
    private ExchangeOrderStatus status;
    //订单方向
    private ExchangeOrderDirection direction;
    //挂单价格
    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 ")
    private BigDecimal price = BigDecimal.ZERO;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    //挂单时间
    private Long time;
    //交易完成时间
    private Long completedTime;
    //取消时间
    private Long canceledTime;
    //是否使用折扣 0 不使用 1使用
    private  String useDiscount ;
    @Transient
    private List<ExchangeOrderDetail> detail;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public boolean isCompleted(){
        if(status != ExchangeOrderStatus.TRADING) {
            return true;
        } else{
            if(type == ExchangeOrderType.MARKET_PRICE && direction == ExchangeOrderDirection.BUY){
                return amount.compareTo(turnover) <= 0;
            }
            else{
                return amount.compareTo(tradedAmount) <= 0;
            }
        }
    }
}
