package com.bizzan.bitrade.service;

import com.bizzan.bitrade.core.Menu;
import com.bizzan.bitrade.dao.AdminDao;
import com.bizzan.bitrade.dao.SysPermissionDao;
import com.bizzan.bitrade.dao.SysRoleDao;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.SysPermission;
import com.bizzan.bitrade.entity.SysRole;
import com.bizzan.bitrade.service.Base.TopBaseService;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GS
 * @date 2017年12月18日
 */
@Service
public class SysRoleService extends TopBaseService<SysRole, SysRoleDao> {

    @Autowired
    private AdminService adminService;

    @Override
    @Autowired
    public void setDao(SysRoleDao dao) {
        super.setDao(dao);
    }

    @Resource
    private SysRoleDao sysRoleDao;

    @Autowired
    private AdminDao adminDao;
    @Resource
    private SysPermissionDao sysPermissionDao;

    public SysRole findOne(Long id) {
        SysRole role = sysRoleDao.findOne(id);
        return role;
    }

    public List<SysPermission> getPermissions(Long roleId) {
        SysRole sysRole = findOne(roleId);
        List<SysPermission> list = sysRole.getPermissions();
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public MessageResult deletes(Long id) {
        List<Admin> list = adminDao.findAllByRoleId(id);
        if (list != null && list.size() > 0) {
            return MessageResult.error("删除失败，请先删除该角色下的所有用户");
        }
        sysRoleDao.delete(id);
        return MessageResult.success("删除成功");
    }

    /**
     * 把权限转换成菜单树
     *
     * @param sysPermissions
     * @param parentId
     * @return
     */
    public List<Menu> toMenus(List<SysPermission> sysPermissions, Long parentId) {
        return sysPermissions.stream()
                .filter(x -> x.getParentId().equals(parentId))
                .sorted(Comparator.comparing(SysPermission::getSort))
                .map(x ->
                        Menu.builder()
                                .id(x.getId())
                                .name(x.getName())
                                .parentId(x.getParentId())
                                .sort(x.getSort())
                                .title(x.getTitle())
                                .description(x.getDescription())
                                .subMenu(toMenus(sysPermissions, x.getId()))
                                .build()

                )
                .collect(Collectors.toList());
    }

    @Override
    public SysRole save(SysRole sysRole) {
        return sysRoleDao.save(sysRole);
    }

    public int updateDetail(SysRole sysRole) {
        return sysRoleDao.updateSysRole(sysRole.getDescription(), sysRole.getRole(), sysRole.getId());
    }

    public List<SysPermission> getAllPermission() {
        return sysPermissionDao.findAll();
    }

    public List<SysRole> getAllSysRole() {
        return sysRoleDao.findAllSysRole();
    }

    public Page<SysRole> findAll(Predicate predicate, Pageable pageable) {
        return sysRoleDao.findAll(predicate, pageable);
    }
}
