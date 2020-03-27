package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统权限
 *
 * @author GS
 * @date 2017年12月18日
 */
@Entity
@Data
@Table(name = "admin_permission")
public class SysPermission {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotBlank(message="权限名不能为空")
    @NotNull(message="权限名不能为空")
    private String title;

    private String description;

    /**
     * 为0表示是菜单
     */
    private Long parentId=0L;

    private Integer sort = 0;

    @NotBlank(message="权限名不能为空")
    @NotNull(message="权限名不能为空")
    private String name;

    @ManyToMany(cascade = CascadeType.PERSIST,mappedBy = "permissions",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SysRole> roles ;
}
