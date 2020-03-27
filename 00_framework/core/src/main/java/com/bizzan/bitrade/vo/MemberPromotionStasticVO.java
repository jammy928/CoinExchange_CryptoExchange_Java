package com.bizzan.bitrade.vo;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import com.bizzan.bitrade.constant.PromotionLevel;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MemberPromotionStasticVO {
    private Long id;

    //邀请者Id
    private Long inviterId;
    
    //受邀者Id
    private Long inviteesId;

    private PromotionLevel level;
    
    private Date createTime;
    
    private int count;
}
