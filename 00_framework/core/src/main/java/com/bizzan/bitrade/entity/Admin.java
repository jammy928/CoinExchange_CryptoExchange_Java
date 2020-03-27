package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;

import com.bizzan.bitrade.constant.CommonStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author GS
 * @date 2017年12月18日
 */
@Entity
@Data
@Table
public class Admin {
    @Excel(name = "用户编号", orderNum = "1", width = 25)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Excel(name = "用户登录名", orderNum = "1", width = 25)
    @NotBlank(message = "用户名不能为空")
    @Column(nullable = false,unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private CommonStatus enable;

    @Excel(name = "用户最后登录时间", orderNum = "1", width = 25)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;

    @Excel(name = "最后登录ip", orderNum = "1", width = 25)
    private String lastLoginIp;

    @NotNull(message = "请选择角色")
    private Long roleId = 1L;


    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name = "department_id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private Department department;

    @Excel(name = "真实姓名", orderNum = "1", width = 25)
    @NotBlank(message = "请填写真实名称")
    private String realName;

    @Excel(name = "联系号码", orderNum = "1", width = 25)
    @NotBlank(message = "请填写联系号码")
    private String mobilePhone;


    private String qq;

    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态
     */
    private CommonStatus status = CommonStatus.NORMAL;

    @Transient
    private String roleName ;
}
