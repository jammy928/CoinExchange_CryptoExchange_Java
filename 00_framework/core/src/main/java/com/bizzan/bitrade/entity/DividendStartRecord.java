package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分红开启记录
 *
 * @author GS
 * @date 2018年03月22日
 */
@Entity
@Data
public class DividendStartRecord {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    private Long start;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    private Long end;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreationTimestamp
    private Date date;

    private String unit;

    @Column(columnDefinition = "decimal(18,2) comment '比例'")
    private BigDecimal rate;
    @Column(columnDefinition = "decimal(18,6) comment '数量'")
    private BigDecimal amount;

    @JoinColumn(name = "admin_id")
    @ManyToOne
    private Admin admin;
}
