package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.RewardRecordType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 奖励记录
 * @author GS
 * @date 2018年03月08日
 */
@Data
@Entity
public class RewardRecord {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @JoinColumn(name = "coin_id", nullable = false)
    @ManyToOne
    private Coin coin;
    private String  remark;
    @Enumerated(EnumType.ORDINAL)
    private RewardRecordType type;
    @Column(columnDefinition = "decimal(18,8) comment '数目'")
    private BigDecimal amount;
    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne
    private Member member;
    /**
     * 创建时间
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
