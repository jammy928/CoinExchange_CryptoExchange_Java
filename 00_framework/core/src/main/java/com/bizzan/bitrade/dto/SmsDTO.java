package com.bizzan.bitrade.dto;

import lombok.Data;

import javax.persistence.*;

/**
 * @Description:
 * @author: GuoShuai
 * @date: create in 21:23 2018/6/27
 * @Modified:
 */
@Entity
@Data
@Table(name = "tb_sms")
public class SmsDTO {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * key
     */
    private String keyId;

    /**
     * Secret
     */
    private String keySecret;

    /**
     * 签名ID(4334)
     */
    private String signId;


    /**
     * 模板ID(6299)
     */
    private String templateId;


    /**
     * 0，可用 1.不可用
     */
    private String smsStatus;

    /**
     * 接入公司名称( chuangrui  , gongxintong)
     */
    private String smsName;







}
