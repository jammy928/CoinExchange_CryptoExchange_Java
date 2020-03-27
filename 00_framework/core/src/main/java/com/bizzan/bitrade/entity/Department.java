package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 部门
 *
 * @author GS
 * @date 2017年12月18日
 */
@Entity
@Data
@Table
public class Department {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @Excel(name = "部门名称", orderNum = "1", width = 20)
    @NotNull(message = "部门名称不能为空")
    @Column(unique = true,nullable = false)
    private String name;

    private String remark;

    private Long leaderId;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
