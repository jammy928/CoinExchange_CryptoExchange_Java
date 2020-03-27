package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 邀请日榜、周榜、月榜排名数据
 * @author shaox
 *
 */
@Entity
@Data
public class MemberInviteStasticRank {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
	
	private Long memberId;
	
	/**
	 * 用户账户标识，手机或邮箱
	 */
	private String userIdentify;
	
    /**
     * 邀请一级好友数量
     */
    private int levelOne;
    
    /**
     * 邀请二级好友数量
     */
    private int levelTwo;
    
    /**
     * 类型：0 = 日榜，1 = 周榜， 2 = 月榜
     */
    @NotNull
    private int type;
    
    /**
     * 是否机器人（0：否，1：是）
     */
    @JsonIgnore
    private int isRobot = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date stasticDate;
}
