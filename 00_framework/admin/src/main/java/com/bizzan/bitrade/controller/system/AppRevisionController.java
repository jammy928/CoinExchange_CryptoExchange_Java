package com.bizzan.bitrade.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.AppRevision;
import com.bizzan.bitrade.model.create.AppRevisionCreate;
import com.bizzan.bitrade.model.screen.AppRevisionScreen;
import com.bizzan.bitrade.model.update.AppRevisionUpdate;
import com.bizzan.bitrade.service.AppRevisionService;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.MessageResult;

import javax.validation.Valid;

/**
 * @author Shaoxianjun
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/2416:31
 */
@RestController
@RequestMapping("system/app-revision")
public class AppRevisionController extends BaseAdminController {
    @Autowired
    private AppRevisionService service;

    //新增
    @PostMapping
    public MessageResult create(@Valid AppRevisionCreate model, BindingResult bindingResult) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        service.save(model);
        return success();
    }

    //更新
    @PutMapping("{id}")
    public MessageResult put(@PathVariable("id") Long id, AppRevisionUpdate model) {
        AppRevision appRevision = service.findById(id);
        Assert.notNull(appRevision, "validate appRevision id!");
        service.update(model, appRevision);
        return success();
    }

    //详情
    @GetMapping("{id}")
    public MessageResult get(@PathVariable("id") Long id) {
        AppRevision appRevision = service.findById(id);
        Assert.notNull(appRevision, "validate appRevision id!");
        return success(appRevision);
    }

    //分页
    @GetMapping("page-query")
    public MessageResult get(PageModel pageModel) {
        return success(service.findAll(null, pageModel));
    }
}
