package com.bizzan.bitrade.controller.system;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.AdminAccessLog;
import com.bizzan.bitrade.entity.QAdmin;
import com.bizzan.bitrade.entity.QAdminAccessLog;
import com.bizzan.bitrade.service.AdminAccessLogService;
import com.bizzan.bitrade.service.AdminService;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.Assert.notNull;

/**
 * @author Shaoxianjun
 * @description 日志管理
 * @date 2019/12/22 17:27
 */
@Slf4j
@RestController
@RequestMapping("/system/access-log")
@Transactional(readOnly = true)
public class AccessLogController extends BaseAdminController {

    @Autowired
    private AdminAccessLogService adminAccessLogService;

    @Autowired
    private AdminService adminService ;

    @RequiresPermissions("system:access-log:all")
    @GetMapping("/all")
    @AccessLog(module = AdminModule.SYSTEM, operation = "所有操作/访问日志AdminAccessLog")
    public MessageResult all() {
        List<AdminAccessLog> adminAccessLogList = adminAccessLogService.queryAll();
        return success(adminAccessLogList);
    }

    @RequiresPermissions("system:access-log:detail")
    @GetMapping("/{id}")
    @AccessLog(module = AdminModule.SYSTEM, operation = "操作/访问日志AdminAccessLog 详情")
    public MessageResult detail(@PathVariable("id") Long id) {
        AdminAccessLog adminAccessLog = adminAccessLogService.queryById(id);
        notNull(adminAccessLog, "validate id!");
        return success(adminAccessLog);
    }

    @RequiresPermissions("system:access-log:page-query")
    @GetMapping("/page-query")
    @AccessLog(module = AdminModule.SYSTEM, operation = "分页查找操作/访问日志AdminAccessLog")
    public MessageResult pageQuery(
            PageModel pageModel,
            @RequestParam(value = "adminName", required = false) String adminName,
            @RequestParam(value = "module", required = false) AdminModule module) {

        List<BooleanExpression> list = new ArrayList<>();
        list.add(QAdmin.admin.id.eq(QAdminAccessLog.adminAccessLog.adminId));
        if (!StringUtils.isEmpty(adminName)) {
            list.add(QAdmin.admin.username.like("%"+adminName+"%"));
        }
        if(module!=null) {
            list.add(QAdminAccessLog.adminAccessLog.module.eq(module));
        }
        Page<AdminAccessLog> all = adminAccessLogService.page(list, pageModel);
        return success(all);
    }


}
