package com.bizzan.bitrade.controller.cms;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysAdvertiseLocation;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.QSysAdvertise;
import com.bizzan.bitrade.entity.SysAdvertise;
import com.bizzan.bitrade.model.screen.SysAdvertiseScreen;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.SysAdvertiseService;
import com.bizzan.bitrade.util.*;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bizzan.bitrade.entity.QSysAdvertise.sysAdvertise;
import static org.springframework.util.Assert.notNull;

/**
 * @author Shaoxianjun
 * @description 系统广告
 * @date 2019/1/6 15:03
 */
@Slf4j
@RestController
@RequestMapping("/cms/system-advertise")
public class AdvertiseController extends BaseAdminController {
    @Autowired
    private SysAdvertiseService sysAdvertiseService;
    @Autowired
    private LocaleMessageSourceService msService;

    @RequiresPermissions("cms:system-advertise:create")
    @PostMapping("/create")
    @AccessLog(module = AdminModule.CMS, operation = "创建系统广告")
    public MessageResult findOne(@Valid SysAdvertise sysAdvertise, BindingResult bindingResult) {
        Date end = DateUtil.strToDate(sysAdvertise.getEndTime());
        Date start = DateUtil.strToDate(sysAdvertise.getStartTime());
        Assert.isTrue(end.after(start), msService.getMessage("START_END_TIME"));
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        sysAdvertise.setSerialNumber(UUIDUtil.getUUID());
        sysAdvertise.setCreateTime(DateUtil.getCurrentDate());

        updateSort(sysAdvertise.getSort(),sysAdvertise.getSysAdvertiseLocation().getOrdinal());
        return success(sysAdvertiseService.save(sysAdvertise));
    }

    @RequiresPermissions("cms:system-advertise:all")
    @PostMapping("/all")
    @AccessLog(module = AdminModule.CMS, operation = "所有系统广告")
    public MessageResult all() {
        List<SysAdvertise> all = sysAdvertiseService.findAll();
        if (all != null & all.size() > 0) {
            return success(all);
        }
        return error("data null");
    }

    @RequiresPermissions("cms:system-advertise:detail")
    @PostMapping("/detail")
    @AccessLog(module = AdminModule.CMS, operation = "系统广告详情")
    public MessageResult findOne(@RequestParam(value = "serialNumber") String serialNumber) {
        SysAdvertise sysAdvertise = sysAdvertiseService.findOne(serialNumber);
        notNull(sysAdvertise, "validate serialNumber!");
        return success(sysAdvertise);
    }

    @RequiresPermissions("cms:system-advertise:update")
    @PostMapping("/update")
    @AccessLog(module = AdminModule.CMS, operation = "更新系统广告")
    public MessageResult update(@Valid SysAdvertise sysAdvertise, BindingResult bindingResult) {
        notNull(sysAdvertise.getSerialNumber(), "validate serialNumber(null)!");
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        SysAdvertise one = sysAdvertiseService.findOne(sysAdvertise.getSerialNumber());
        notNull(one, "validate serialNumber!");
        updateSort(sysAdvertise.getSort(), sysAdvertise.getSysAdvertiseLocation().getOrdinal());
        sysAdvertiseService.save(sysAdvertise);
        return success();
    }



    @RequiresPermissions("cms:system-advertise:deletes")
    @PostMapping("/deletes")
    @AccessLog(module = AdminModule.CMS, operation = "批量删除系统广告")
    public MessageResult delete(@RequestParam(value = "ids") String[] ids) {
        sysAdvertiseService.deleteBatch(ids);
        return success();
    }


    @RequiresPermissions("cms:system-advertise:page-query")
    @PostMapping("/page-query")
    @AccessLog(module = AdminModule.CMS, operation = "分页查询系统广告")
    public MessageResult pageQuery(PageModel pageModel, SysAdvertiseScreen screen) {
        Predicate predicate = getPredicate(screen);
        if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("createTime");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Page<SysAdvertise> all = sysAdvertiseService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    private Predicate getPredicate(SysAdvertiseScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        if (screen.getStatus() != null) {
            booleanExpressions.add(QSysAdvertise.sysAdvertise.status.eq(screen.getStatus()));
        }
        if (screen.getSysAdvertiseLocation() != null) {
            booleanExpressions.add(QSysAdvertise.sysAdvertise.sysAdvertiseLocation.eq(screen.getSysAdvertiseLocation()));
        }
        if (StringUtils.isNotBlank(screen.getSerialNumber())) {
            booleanExpressions.add(QSysAdvertise.sysAdvertise.serialNumber.like("%" + screen.getSerialNumber() + "%"));
        }
        return PredicateUtils.getPredicate(booleanExpressions);
    }

    @RequiresPermissions("cms:system-advertise:top")
    @PostMapping("top")
    @AccessLog(module = AdminModule.CMS, operation = "广告置顶")
    public MessageResult toTop(@RequestParam("serialNum") String serialNum) {
        SysAdvertise advertise = sysAdvertiseService.findOne(serialNum);
        int a = sysAdvertiseService.getMaxSort();
        advertise.setSort(a + 1);
        sysAdvertiseService.save(advertise);
        return success();
    }


    @RequiresPermissions("cms:system-advertise:out-excel")
    @GetMapping("/out-excel")
    @AccessLog(module = AdminModule.CMS, operation = "导出系统广告Excel")
    public MessageResult outExcel(
            @RequestParam(value = "serialNumber", required = false) String serialNumber,
            @RequestParam(value = "sysAdvertiseLocation", required = false) SysAdvertiseLocation sysAdvertiseLocation,
            @RequestParam(value = "status", required = false) CommonStatus status,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Predicate> predicateList = getPredicateList(serialNumber, sysAdvertiseLocation, status);
        List list = sysAdvertiseService.query(predicateList, null, null).getContent();
        return new FileUtil().exportExcel(request, response, list, "sysAdvertise");
    }

    private List<Predicate> getPredicateList(String serialNumber, SysAdvertiseLocation sysAdvertiseLocation, CommonStatus status) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(serialNumber)) {
            predicates.add(sysAdvertise.serialNumber.eq(serialNumber));
        }
        if (sysAdvertiseLocation != null) {
            predicates.add(sysAdvertise.sysAdvertiseLocation.eq(sysAdvertiseLocation));
        }
        if (status != null) {
            predicates.add(sysAdvertise.status.eq(status));
        }
        return predicates;
    }


    /***
     * 广告排序
     * 获取所有广告，没有该序号设定，存在该序号时，大于该序号的全部递增
     * @param sort
     */
    public  void  updateSort(int  sort ,int cate){
        List<SysAdvertise> list=sysAdvertiseService.querySysAdvertise(sort,cate);
        //筛选大于该序号的广告
        for(int i=0;i<list.size();i++){
            list.get(i).setSort(list.get(i).getSort()+1);
        }
    }
}
