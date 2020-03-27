package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author GS
 * @description 会员钱包
 * @date 2018/1/2 15:28
 */
@Entity
@Data
@Table(uniqueConstraints ={@UniqueConstraint(columnNames={"memberId", "coin_id"})})
public class MemberWallet {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;
    /**
     * 可用余额
     */
    @Column(columnDefinition = "decimal(26,16) comment '可用余额'")
    private BigDecimal balance;
    /**
     * 冻结余额
     */
    @Column(columnDefinition = "decimal(26,16) comment '冻结余额'")
    private BigDecimal frozenBalance;

    /**
     * 待释放总量
     */
    @Column(columnDefinition = "decimal(18,8) comment '待释放总量'")
    private BigDecimal toReleased;

    /**
     * 充值地址
     */
    private String address;

    
    @JsonIgnore
    @Version
    private int version;

    /**
     * 钱包是否锁定，0否，1是。锁定后
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int default 0 comment '钱包不是锁定'")
    private BooleanEnum isLock = BooleanEnum.IS_FALSE;
    
    /**
     * EOS、XRP等币种需要填写Memo时生成
     */
    @Transient
    private String memo;
}
