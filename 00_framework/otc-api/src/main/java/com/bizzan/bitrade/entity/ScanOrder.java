package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.AdvertiseType;
import com.bizzan.bitrade.constant.OrderStatus;
import com.bizzan.bitrade.entity.Order;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author GS
 * @date 2018年01月19日
 */
@Builder
@Data
public class ScanOrder {
    private String orderSn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private String unit;
    private AdvertiseType type;
    private String name;
    private BigDecimal price;
    private BigDecimal money;
    private BigDecimal commission;
    private BigDecimal amount;
    private OrderStatus status;
    private Long memberId;
    private String avatar;

    public static ScanOrder toScanOrder(Order order, Long id) {
        return ScanOrder.builder().orderSn(order.getOrderSn())
                .createTime(order.getCreateTime())
                .unit(order.getCoin().getUnit())
                .price(order.getPrice())
                .amount(order.getNumber())
                .money(order.getMoney())
                .status(order.getStatus())
                .commission(id.equals(order.getMemberId())?order.getCommission():BigDecimal.ZERO)
                .name(order.getCustomerId().equals(id) ? order.getMemberName() : order.getCustomerName())
                .memberId(order.getCustomerId().equals(id) ? order.getMemberId():order.getCustomerId())
                .type(judgeType(order.getAdvertiseType(), order, id))
                .build();
    }

    public static AdvertiseType judgeType(AdvertiseType type, Order order, Long id) {
        if (type.equals(AdvertiseType.BUY) && id.equals(order.getMemberId())) {
            return AdvertiseType.BUY;
        } else if (type.equals(AdvertiseType.BUY) && id.equals(order.getCustomerId())) {
            return AdvertiseType.SELL;
        } else if (type.equals(AdvertiseType.SELL) && id.equals(order.getCustomerId())) {
            return AdvertiseType.BUY;
        } else  {
            return AdvertiseType.SELL;
        }
    }
}
