package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充币记录
 */
@Data
@Entity
@Table(uniqueConstraints ={@UniqueConstraint(columnNames={"txid", "address"})})
public class MemberDeposit {
    @Id
    @GeneratedValue
    private Long id;
    private Long memberId;
    @Column(columnDefinition = "decimal(18,8) default 0")
    private BigDecimal amount;
    private String unit;
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private String txid;
    private String address;

    @Transient
    private String username ;
}
