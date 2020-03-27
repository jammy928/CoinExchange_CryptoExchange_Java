package com.bizzan.bitrade.controller.system;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.core.Menu;
import com.bizzan.bitrade.entity.SysRole;
import com.bizzan.bitrade.service.SysPermissionService;
import com.bizzan.bitrade.service.SysRoleService;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.MessageResult;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Shaoxianjun
 * @date 2018年12月20日
 */
@RestController
@RequestMapping(value = "system/role")
public class RoleController extends BaseAdminController {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 创建或修改角色
     *
     * @param sysRole
     * @param bindingResult
     * @return
     */


    @RequiresPermissions("system:role:merge")
    @RequestMapping("merge")
    @Transactional(rollbackFor = Exception.class)
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建或修改角色SysRole")
    public MessageResult mergeRole(@Valid SysRole sysRole, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        sysRole = sysRoleService.save(sysRole);
        if (sysRole != null) {
            result = success("操作成功");
            result.setData(sysRole);
            return result;
        } else {
            return MessageResult.error(500, "操作失败");
        }

    }

    /**
     * 全部权限树
     *
     * @return
     */
    @RequiresPermissions("system:role:permission:all")
    @RequestMapping("permission/all")
    @AccessLog(module = AdminModule.SYSTEM, operation = "全部权限树Menu")
    public MessageResult allMenu() {
        List<Menu> list = sysRoleService.toMenus(sysRoleService.getAllPermission(), 0L);
        MessageResult result = success("success");
        result.setData(list);
        return result;
    }

    /**
     * 角色拥有的权限
     *
     * @param roleId
     * @return
     */
    @RequiresPermissions("system:role:permission")
    @RequestMapping("permission")
    @AccessLog(module = AdminModule.SYSTEM, operation = "角色拥有的权限Menu")
    public MessageResult roleAllPermission(Long roleId) {
        List<Menu> content = sysRoleService.toMenus(sysRoleService.findOne(roleId).getPermissions(), 0L);
        MessageResult result = success();
        result.setData(content);
        return result;
    }

    /**
     * 更改角色权限
     *
     * @param roleId
     * @param permissionId
     * @return
     */
    //@RequiresPermissions("system:role:permission:update")
   /* @RequestMapping("permission/update")
    @Transactional(rollbackFor = Exception.class)
    @AccessLog(module = AdminModule.SYSTEM, operation = "更改角色拥有的权限Menu")
    public MessageResult updateRolePermission(Long roleId, Long[] permissionId) {
        SysRole sysRole = sysRoleService.findOne(roleId);
        if (permissionId != null) {
            List<SysPermission> list = Arrays.stream(permissionId)
                    .map(x -> sysPermissionService.findOne(x))
                    .collect(Collectors.toList());
            sysRole.setPermissions(list);
        } else {
            sysRole.setPermissions(null);
        }
        return success("操作成功");
    }*/

    /**
     * 全部角色
     *
     * @return
     */
    @RequiresPermissions("system:role:all")
    @RequestMapping("all")
    @AccessLog(module = AdminModule.SYSTEM, operation = "所有角色SysRole")
    public MessageResult getAllRole(PageModel pageModel) {
        Page<SysRole> all = sysRoleService.findAll(null, pageModel.getPageable());
        return success(all);
    }

    /**
     * 删除角色
     *
     * @return
     */
    @RequiresPermissions("system:role:deletes")
    @RequestMapping("deletes")
    @AccessLog(module = AdminModule.SYSTEM, operation = "删除角色SysRole")
    public MessageResult deletes(Long id) {
        MessageResult result = sysRoleService.deletes(id);
        return result;
    }


}
