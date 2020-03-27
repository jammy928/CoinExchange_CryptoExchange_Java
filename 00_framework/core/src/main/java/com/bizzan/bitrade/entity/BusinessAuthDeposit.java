package com.bizzan.bitrade.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import com.bizzan.bitrade.constant.CommonStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商家认证缴纳押金的币种和数量
 * @author Shaoxianjun
 * @date 2019/5/5
 */
@Entity
@Data
@Table(name = "business_auth_deposit")
public class BusinessAuthDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 押金币种
     */
    @OneToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;
    /**
     * 押金数额
     */
    @Column(columnDefinition = "decimal(18,8) comment '保证金数额'")
    private BigDecimal amount;

    private Date createTime;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    /**
     * 启用、禁用
     */
    @Enumerated(EnumType.ORDINAL)
    private CommonStatus status = CommonStatus.NORMAL;
}
