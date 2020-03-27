package com.bizzan.bitrade.controller.system;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.AnnouncementClassification;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.Announcement;
import com.bizzan.bitrade.entity.QAnnouncement;
import com.bizzan.bitrade.service.AnnouncementService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


/**
 * @author Shaoxianjun
 * @description 公告
 * @date 2019/3/5 15:25
 */
@RestController
@RequestMapping("system/announcement")
public class AnnouncementController extends BaseController {
    @Autowired
    private AnnouncementService announcementService;
    @Autowired
    private LocaleMessageSourceService messageSource;

    @RequiresPermissions("system:announcement:create")
    @PostMapping("create")
    public MessageResult create(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String lang,
            @RequestParam AnnouncementClassification announcementClassification,
            @RequestParam("isShow") Boolean isShow,
            @RequestParam(value = "imgUrl", required = false) String imgUrl) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setIsShow(isShow);
        announcement.setLang(lang);
        announcement.setAnnouncementClassification(announcementClassification);
        announcement.setImgUrl(imgUrl);
        announcementService.save(announcement);
        return success(messageSource.getMessage("SUCCESS"));
    }

    @RequiresPermissions("system:announcement:top")
    @PostMapping("top")
    @AccessLog(module = AdminModule.CMS, operation = "公告置顶")
    public MessageResult toTop(@RequestParam("id")long id){
        Announcement announcement = announcementService.findById(id);
        int a = announcementService.getMaxSort();
        announcement.setSort(a+1);
        announcement.setIsTop("0");
        announcementService.save(announcement);
        return success(messageSource.getMessage("SUCCESS"));
    }


    /**
     * 取消公告置顶
     * @param id
     * @return
     */
    @RequiresPermissions("system:announcement:dwon")
    @PostMapping("down")
    @AccessLog(module = AdminModule.CMS, operation = "公告取消置顶")
    public MessageResult toDown(@RequestParam("id")long id){
        Announcement announcement = announcementService.findById(id);
        announcement.setIsTop("1");
        announcementService.save(announcement);
        return success();
    }

    @RequiresPermissions("system:announcement:page-query")
    @GetMapping("page-query")
    public MessageResult page(
            PageModel pageModel,
            @RequestParam(required = false) Boolean isShow) {
        //条件
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        if (isShow != null) {
            booleanExpressions.add(QAnnouncement.announcement.isShow.eq(isShow));
        }
        Predicate predicate = PredicateUtils.getPredicate(booleanExpressions);
        Page<Announcement> all = announcementService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    @RequiresPermissions("system:announcement:deletes")
    @PatchMapping("deletes")
    public MessageResult deleteOne(@RequestParam("ids") Long[] ids) {
        announcementService.deleteBatch(ids);
        return success();
    }

    @RequiresPermissions("system:announcement:detail")
    @GetMapping("{id}/detail")
    public MessageResult detail(
            @PathVariable Long id) {
        Announcement announcement = announcementService.findById(id);
        Assert.notNull(announcement, "validate id!");
        return success(announcement);
    }


    @RequiresPermissions("system:announcement:update")
    @PutMapping("{id}/update")
    public MessageResult update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Boolean isShow,
            @RequestParam String lang,
            @RequestParam AnnouncementClassification announcementClassification,
            @RequestParam(value = "imgUrl", required = false) String imgUrl) {
        Announcement announcement = announcementService.findById(id);
        Assert.notNull(announcement, "validate id!");
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setIsShow(isShow);
        announcement.setLang(lang);
        announcement.setAnnouncementClassification(announcementClassification);
        announcement.setImgUrl(imgUrl);
        announcementService.save(announcement);
        return success();
    }

    @RequiresPermissions("system:announcement:turn-off")
    @PatchMapping("{id}/turn-off")
    public MessageResult turnOff(@PathVariable Long id) {
        Announcement announcement = announcementService.findById(id);
        Assert.notNull(announcement, "validate id!");
        announcement.setIsShow(false);
        announcementService.save(announcement);
        return success();
    }

    @RequiresPermissions("system:announcement:turn-on")
    @PatchMapping("{id}/turn-on")
    public MessageResult turnOn(@PathVariable("id") Long id) {
        Announcement announcement = announcementService.findById(id);
        Assert.notNull(announcement, "validate id!");
        announcement.setIsShow(true);
        announcementService.save(announcement);
        return success();
    }

}
