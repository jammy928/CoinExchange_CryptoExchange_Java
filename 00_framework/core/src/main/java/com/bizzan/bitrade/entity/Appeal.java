package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.AppealStatus;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 申诉
 *
 * @author GS
 * @date 2018年01月22日
 */
@Entity
@Data
public class Appeal {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @OneToOne
    private Order order;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 处理时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealWithTime;

    @Column(length = 500)
    private String remark;

    /**
     * 申诉发起者id
     */
    private Long initiatorId;
    /**
     * 申诉关联者id
     */
    private Long associateId;

    /**
     * 发起者是否胜诉
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = true)
    private BooleanEnum isSuccess ;
    /**
     * 处理状态
     */
    @Enumerated(EnumType.ORDINAL)
    private AppealStatus status = AppealStatus.NOT_PROCESSED;

    /**
     * 处理者
     */
    @JoinColumn(name = "admin_id")
    @ManyToOne
    private Admin admin;
}
