package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author GS
 * @description
 * @date 2018/1/25 17:19
 */
@Entity
@Data
@Table(name = "website_information")
public class WebsiteInformation {
    @Id
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * logo
     */
    private String logo;
    /**
     * 地址栏图标
     */
    private String addressIcon;
    /**
     * 网站网址
     */
    private String url;
    /**
     * 关键词
     */
    private String keywords;
    /**
     * 描述
     */
    private String description;
    /**
     * 版权信息
     */
    private String copyright;
    /**
     * 邮编
     */
    private String postcode;
    /**
     * 联系方式
     */
    private String contact;
    /**
     * 其他信息
     */
    @Column(columnDefinition = "TEXT")
    private String otherInformation;
}
