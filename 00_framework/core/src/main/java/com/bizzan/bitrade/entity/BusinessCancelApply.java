package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="bussiness_cancel_apply")
@ToString
public class BusinessCancelApply {

    public BusinessCancelApply(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id ;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @Enumerated(value = EnumType.ORDINAL)
    private CertifiedBusinessStatus status ;

    private String depositRecordId ;

    @CreationTimestamp
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelApplyTime ;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime ;

    /**
     * 退保原因
     *
     */
    private String reason ;

    /**
     * 审核失败理由
     */
    private String detail ;

    @Transient
    private DepositRecord depositRecord ;
}
