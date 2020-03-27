package com.bizzan.bitrade.controller.otc;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.AdvertiseControlStatus;
import com.bizzan.bitrade.constant.AdvertiseType;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.Advertise;
import com.bizzan.bitrade.entity.QAdvertise;
import com.bizzan.bitrade.model.screen.AdvertiseScreen;
import com.bizzan.bitrade.service.AdvertiseService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.FileUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Shaoxianjun
 * @description 后台广告web层
 * @date 2019/1/3 9:42
 */
@RestController
@RequestMapping("/otc/advertise")
public class AdminAdvertiseController extends BaseAdminController {

    @Autowired
    private AdvertiseService advertiseService;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("otc:advertise:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.OTC, operation = "后台广告Advertise详情")
    public MessageResult detail(Long id) {
        if (id == null) {
            return error("id必传");
        }
        Advertise one = advertiseService.findOne(id);
        if (one == null) {
            return error("没有此id的广告");
        }
        return success(messageSource.getMessage("SUCCESS"), one);
    }

    @RequiresPermissions("otc:advertise:alter-status")
    @PostMapping("alter-status")
    @AccessLog(module = AdminModule.OTC, operation = "修改后台广告Advertise状态")
    public MessageResult statue(
            @RequestParam(value = "ids") Long[] ids,
            @RequestParam(value = "status") AdvertiseControlStatus status) {
        advertiseService.turnOffBatch(status,ids);
        return success(messageSource.getMessage("SUCCESS"));
    }

    @RequiresPermissions("otc:advertise:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.OTC, operation = "分页查找后台广告Advertise")
    public MessageResult page(PageModel pageModel, AdvertiseScreen screen) {
        Predicate predicate = getPredicate(screen);
        Page<Advertise> all = advertiseService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    @RequiresPermissions("otc:advertise:out-excel")
    @GetMapping("out-excel")
    @AccessLog(module = AdminModule.OTC, operation = "导出后台广告Advertise Excel")
    public MessageResult outExcel(
            @RequestParam(value = "startTime", required = false) Date startTime,
            @RequestParam(value = "endTime", required = false) Date endTime,
            @RequestParam(value = "advertiseType", required = false) AdvertiseType advertiseType,
            @RequestParam(value = "realName", required = false) String realName,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<BooleanExpression> booleanExpressionList = getBooleanExpressionList(startTime, endTime, advertiseType, realName);
        List list = advertiseService.queryWhereOrPage(booleanExpressionList, null, null).getContent();
        return new FileUtil().exportExcel(request, response, list, "order");
    }

    // 获得条件
    private List<BooleanExpression> getBooleanExpressionList(
            Date startTime, Date endTime, AdvertiseType advertiseType, String realName) {
        QAdvertise qEntity = QAdvertise.advertise;
        List<BooleanExpression> booleanExpressionList = new ArrayList();
        booleanExpressionList.add(qEntity.status.in(AdvertiseControlStatus.PUT_ON_SHELVES, AdvertiseControlStatus.PUT_OFF_SHELVES));
        if (startTime != null) {
            booleanExpressionList.add(qEntity.createTime.gt(startTime));
        }
        if (endTime != null) {
            booleanExpressionList.add(qEntity.createTime.lt(endTime));
        }
        if (advertiseType != null) {
            booleanExpressionList.add(qEntity.advertiseType.eq(advertiseType));
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(realName)) {
            booleanExpressionList.add(qEntity.member.realName.like("%" + realName + "%"));
        }
        return booleanExpressionList;
    }


    private Predicate getPredicate(AdvertiseScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        if(screen.getStatus()!=AdvertiseControlStatus.TURNOFF&&screen.getStatus()!=null) {
            booleanExpressions.add(QAdvertise.advertise.status.eq(screen.getStatus()));
        }
        if (screen.getStatus() == null){
            booleanExpressions.add(QAdvertise.advertise.status.eq(AdvertiseControlStatus.PUT_ON_SHELVES).or(QAdvertise.advertise.status.eq(AdvertiseControlStatus.PUT_OFF_SHELVES)));
        }
        if (screen.getAdvertiseType() != null) {
            booleanExpressions.add(QAdvertise.advertise.advertiseType.eq(screen.getAdvertiseType()));
        }
        if (StringUtils.isNotBlank(screen.getAccount())) {
            booleanExpressions.add(QAdvertise.advertise.member.realName.like("%" + screen.getAccount() + "%")
                                    .or(QAdvertise.advertise.member.username.like("%" + screen.getAccount() + "%"))
                                    .or(QAdvertise.advertise.member.mobilePhone.like((screen.getAccount()+"%")))
                                    .or(QAdvertise.advertise.member.email.like((screen.getAccount()+"%"))));
        }
        if(screen.getPayModel()!=null) {
            booleanExpressions.add(QAdvertise.advertise.payMode.contains(screen.getPayModel()));
        }
        return PredicateUtils.getPredicate(booleanExpressions);
    }


}
