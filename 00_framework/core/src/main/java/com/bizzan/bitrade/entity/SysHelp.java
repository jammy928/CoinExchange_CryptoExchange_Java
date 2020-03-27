package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.SysHelpClassification;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author GS
 * @description 系统帮助
 * @date 2018/1/9 9:38
 */
@Entity
@Data
@Table(name = "sys_help")
public class SysHelp {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Excel(name = "编号", orderNum = "1", width = 20)
    private Long id;

    @Excel(name = "帮助标题", orderNum = "1", width = 20)
    @NotBlank(message = "标题不能为空")
    private String title;

    @Excel(name = "分类", orderNum = "1", width = 20)
    @NotNull(message = "分类不能为空")
    private SysHelpClassification sysHelpClassification;

    @Excel(name = "图片地址", orderNum = "1", width = 20)
    private String imgUrl = "";

    @Excel(name = "创建时间", orderNum = "1", width = 20)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @NotNull(message = "状态不能为空")
    private CommonStatus status = CommonStatus.NORMAL;

    /**
     * 类型不为二维码时有效，为新手入门，充值指南，交易指南等的具体内容
     */
    @Column(columnDefinition="TEXT")
    @Basic(fetch=FetchType.LAZY)
    private String content = "";

    private String author = "admin";

    private int sort = 0 ;

    /**
     * 是否置顶（0，置顶  1，不置顶（默认））
     */
    private String isTop;
    
    /**
     * 文章语言
     */
    private String lang = "CN";

}
