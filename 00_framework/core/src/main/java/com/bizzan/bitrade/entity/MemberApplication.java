package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.AuditStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author GS
 * @description 会员审核信息
 * @date 2017/12/26 14:35
 */
@Entity
@Table(name = "member_application")
@Data
public class MemberApplication {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String realName;
    private String idCard;
    /**
     * 身份证 正面
     */
    @NotBlank(message = "身份证正面图片不能为空")
    private String identityCardImgFront;
    /**
     * 身份证 反面
     */
    @NotBlank(message = "身份证反面图片不能为空")
    private String identityCardImgReverse;
    /**
     * 身份证 手持
     */
    @NotBlank(message = "手持身份证图片不能为空")
    private String identityCardImgInHand;

    /**
     *  审核状态
     */
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private AuditStatus auditStatus;

    /**
     * 审核信息所有者
     */
    @JoinColumn(name = "member_id",nullable = false)
    @ManyToOne
    private Member member;

    /**
     * 驳回理由
     */
    private String rejectReason;

    /**
     * 创建时间
     */

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
