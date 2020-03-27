package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.Announcement;
import com.bizzan.bitrade.entity.QAnnouncement;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.service.AnnouncementService;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author GS
 * @description
 * @date 2018/3/5 15:25
 */
@RestController
@RequestMapping("announcement")
public class AnnouncementController extends BaseController {
    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private RedisTemplate redisTemplate;


    /*@ApiOperation("全部")
    @GetMapping
    public MessageResult all() {
        List<Announcement> announcementList = announcementService.findAll();
        return success(announcementList);
    }*/

    @PostMapping("page")
    public MessageResult page(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "lang", defaultValue = "CN") String lang
    ) {
        //条件
        ArrayList<Predicate> predicates = new ArrayList<>();
        predicates.add(QAnnouncement.announcement.isShow.eq(true));
        predicates.add(QAnnouncement.announcement.lang.eq(lang));
        //排序
        ArrayList<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        orderSpecifiers.add(QAnnouncement.announcement.createTime.desc());
        //查
        PageResult<Announcement> pageResult = announcementService.queryDsl(pageNo, pageSize, predicates, QAnnouncement.announcement, orderSpecifiers);
        List<Announcement> rlist = pageResult.getContent();
        for(int i = 0; i < rlist.size(); i++) {
        	rlist.get(i).setContent(null);
        }
        pageResult.setContent(rlist);
        return success(pageResult);
    }

    @GetMapping("{id}")
    public MessageResult detail(@PathVariable("id") Long id) {
        Announcement announcement = announcementService.findById(id);
        Assert.notNull(announcement, "validate id!");
        return success(announcement);
    }

    /**
     * 根据ID获取当前公告及上一条和下一条
     * @param id
     * @return
     */
    @RequestMapping(value = "more",method = RequestMethod.POST)
    public MessageResult moreDetail(@RequestParam("id")Long id, @RequestParam("lang")String lang){
        ValueOperations redisOperations = redisTemplate.opsForValue();
        JSONObject result  = (JSONObject) redisOperations.get(SysConstant.NOTICE_DETAIL+id);
        if ( result != null){
            return success(result);
        }else {
            JSONObject resultObj = new JSONObject();
            Announcement announcement = announcementService.findById(id);
            Assert.notNull(announcement, "validate id!");
            resultObj.put("info",announcement);
            resultObj.put("back",announcementService.getBack(id, lang));
            resultObj.put("next",announcementService.getNext(id, lang));
            redisOperations.set(SysConstant.NOTICE_DETAIL+id,resultObj,SysConstant.NOTICE_DETAIL_EXPIRE_TIME, TimeUnit.SECONDS);
            return success(resultObj);
        }
    }



}
