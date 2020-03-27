package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 意见反馈
 *
 * @author GS
 * @date 2018年03月19日
 */
@Entity
@Data
public class Feedback {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private String remark;
    /**
     * 反馈者
     */
    @JoinColumn(name = "member_id",nullable = false)
    @ManyToOne
    private Member member;
}
