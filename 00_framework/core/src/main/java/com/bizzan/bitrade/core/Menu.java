package com.bizzan.bitrade.core;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 目录菜单
 *
 * @author GS
 * @date 2017年12月19日
 */
@Data
@Builder
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long parentId;
    private String name;
    private String url;
    private Integer sort;
    private List<Menu> subMenu;
    private String title;
    private String description;
}
