package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.SignStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author GS
 * @Description: 签到活动
 * @date 2018/5/310:54
 */
@Entity
@Data
@Table
@ToString
public class Sign {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 赠送币种
     */
    @OneToOne
    @JoinColumn(name = "coin_name")
    private Coin coin;

    /**
     * 赠送数目
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private BigDecimal amount;

    /**
     * 结束日期
     */
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime;

    /**
     * 签到活动状态
     */
    private SignStatus status = SignStatus.UNDERWAY;

}
