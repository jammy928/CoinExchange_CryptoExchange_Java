package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.bizzan.bitrade.entity.WithdrawRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author GS
 * @date 2018年03月01日
 */
@Builder
@Data
public class ScanWithdrawRecord {

    /**
     * 申请总数量
     */
    private BigDecimal totalAmount;
    /**
     * 手续费
     */
    private BigDecimal fee;
    /**
     * 预计到账数量
     */
    private BigDecimal arrivedAmount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealTime;
    /**
     * 提现状态
     */
    private WithdrawStatus status ;
    /**
     * 是否是自动提现
     */
    private BooleanEnum isAuto;

    private String unit;

    /** 能不能自动提现 */
    private BooleanEnum canAutoWithdraw;

    /**
     * 交易编号
     */
    private String transactionNumber;
    /**
     * 提现地址
     */
    private String address;

    private String remark;

    public static ScanWithdrawRecord toScanWithdrawRecord(WithdrawRecord withdrawRecord) {
        return ScanWithdrawRecord.builder().totalAmount(withdrawRecord.getTotalAmount())
                .createTime(withdrawRecord.getCreateTime())
                .unit(withdrawRecord.getCoin().getUnit())
                .dealTime(withdrawRecord.getDealTime())
                .fee(withdrawRecord.getFee())
                .arrivedAmount(withdrawRecord.getArrivedAmount())
                .status(withdrawRecord.getStatus())
                .isAuto(withdrawRecord.getIsAuto())
                .address(withdrawRecord.getAddress())
                .remark(withdrawRecord.getRemark())
                .canAutoWithdraw(withdrawRecord.getCanAutoWithdraw())
                .transactionNumber(withdrawRecord.getTransactionNumber())
                .build();
    }
}
