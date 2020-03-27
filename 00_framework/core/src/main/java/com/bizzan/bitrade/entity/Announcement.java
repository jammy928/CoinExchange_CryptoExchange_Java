package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.AnnouncementClassification;
import com.bizzan.bitrade.constant.SysHelpClassification;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author GS
 * @description 公告
 * @date 2018/3/5 14:59
 */
@Entity
@Data
@Table
public class Announcement {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull(message = "标题不能为空")
    private String title;

    @Column(columnDefinition="TEXT")
    @Basic(fetch=FetchType.LAZY)
    private String content;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    @Excel(name = "分类", orderNum = "1", width = 20)
    @NotNull(message = "分类不能为空")
    private AnnouncementClassification announcementClassification;
    
    //是否显示
    private Boolean isShow;

    //语言
    private String lang;
    
    //是否置顶
    private String isTop;

    @Column(nullable = true)
    private String imgUrl;

    private int sort = 0 ;
}
