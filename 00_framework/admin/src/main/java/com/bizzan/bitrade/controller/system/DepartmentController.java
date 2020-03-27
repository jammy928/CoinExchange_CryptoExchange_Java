package com.bizzan.bitrade.controller.system;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.Department;
import com.bizzan.bitrade.service.DepartmentService;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.MessageResult;

import javax.validation.Valid;

/**
 * @author Shaoxianjun
 * @date 2018年12月20日
 */
@RestController
@RequestMapping(value = "/system/department")
public class DepartmentController extends BaseAdminController {

    @Autowired
    private DepartmentService departmentService;


    /**
     * 创建或更新部门
     *
     * @param department
     * @return
     */
    @RequiresPermissions("system:department:merge")
    @RequestMapping("merge")
    @AccessLog(module = AdminModule.SYSTEM, operation = "创建或更新部门Department")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult save(@Valid Department department, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        if (department.getId() != null) {
            department.setCreateTime(departmentService.findOne(department.getId()).getCreateTime());
        }
        departmentService.save(department);
        return success();
    }

    /**
     * 部门详情
     *
     * @param departmentId
     * @return
     */
    @RequiresPermissions("system:department:detail")
    @RequestMapping("detail")
    @AccessLog(module = AdminModule.SYSTEM, operation = "部门Department详情")
    public MessageResult detail(Long departmentId) {
        Department department = departmentService.getDepartmentDetail(departmentId);
        return success(department);
    }

    /**
     * 全部部门
     *
     * @return
     */
    @RequiresPermissions("system:department:all")
    @RequestMapping("all")
    @AccessLog(module = AdminModule.SYSTEM, operation = "所有部门Department")
    public MessageResult allDepartment(PageModel pageModel) {
        Page<Department> all = departmentService.findAll(null, pageModel.getPageable());
        return success(all);
    }

    @RequiresPermissions("system:department:deletes")
    @RequestMapping("deletes")
    @AccessLog(module = AdminModule.SYSTEM, operation = "批量删除部门")
    public MessageResult deletes(@RequestParam(value = "id") Long id) {
        MessageResult result = departmentService.deletes(id);
        return result;
    }

}
