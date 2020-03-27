package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * 银行卡信息
 *
 * @author GS
 * @date 2018年01月16日
 */
@Data
@Embeddable
public class BankInfo implements Serializable {
    private static final long serialVersionUID = -5602439827000928628L;
    /**
     * 银行
     */
    private String bank;
    /**
     * 支行
     */
    private String branch;
    /**
     * 银行卡号
     */
    private String cardNo;
}
