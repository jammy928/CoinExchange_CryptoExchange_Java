package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.Platform;
import com.bizzan.bitrade.constant.SysAdvertiseLocation;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.constant.SysHelpClassification;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.AppRevision;
import com.bizzan.bitrade.entity.SysAdvertise;
import com.bizzan.bitrade.entity.SysHelp;
import com.bizzan.bitrade.entity.WebsiteInformation;
import com.bizzan.bitrade.service.AppRevisionService;
import com.bizzan.bitrade.service.SysAdvertiseService;
import com.bizzan.bitrade.service.SysHelpService;
import com.bizzan.bitrade.service.WebsiteInformationService;
import com.bizzan.bitrade.util.MessageResult;
import com.netflix.discovery.converters.Auto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author GS
 * @date 2018年02月05日
 */
@RestController
@RequestMapping("/ancillary")
@Slf4j
public class AideController extends BaseController{
    @Autowired
    private WebsiteInformationService websiteInformationService;

    @Autowired
    private SysAdvertiseService sysAdvertiseService;

    @Autowired
    private SysHelpService sysHelpService;
    @Autowired
    private AppRevisionService appRevisionService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 站点信息
     *
     * @return
     */
    @RequestMapping("/website/info")
    public MessageResult keyWords() {
        WebsiteInformation websiteInformation = websiteInformationService.fetchOne();
        MessageResult result = MessageResult.success();
        result.setData(websiteInformation);
        return result;
    }

    /**
     * 系统广告
     *
     * @return
     */
    @RequestMapping("/system/advertise")
    public MessageResult sysAdvertise(@RequestParam(value = "sysAdvertiseLocation", required = false) SysAdvertiseLocation sysAdvertiseLocation,
    								  @RequestParam(value = "lang", required = false) String lang) {
        List<SysAdvertise> list = sysAdvertiseService.findAllNormal(sysAdvertiseLocation, lang);
        MessageResult result = MessageResult.success();
        result.setData(list);
        return result;
    }


    /**
     * 系统帮助
     *
     * @return
     */
    @RequestMapping("/system/help")
    public MessageResult sysHelp(@RequestParam(value = "sysHelpClassification", required = false) SysHelpClassification sysHelpClassification) {
        List<SysHelp> list = null;
        if (sysHelpClassification == null) {
            list = sysHelpService.findAll();
        } else {
            list = sysHelpService.findBySysHelpClassification(sysHelpClassification);
        }
        MessageResult result = MessageResult.success();
        result.setData(list);
        return result;
    }

    /**
     * 查询帮助中心首页数据
     * @param total
     * @return
     */
    @RequestMapping(value = "more/help",method = RequestMethod.POST)
    public MessageResult sysAllHelp(@RequestParam(value = "total",defaultValue = "6")Integer total,
    								@RequestParam(value = "lang",defaultValue = "CN")String lang){

        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<JSONObject> result = (List<JSONObject>) valueOperations.get(SysConstant.SYS_HELP+lang);
        if (result != null){
            return success(result);
        } else {
            //HELP("新手指南")
            List<JSONObject> jsonResult = new ArrayList<>();
            Page<SysHelp> sysHelpPage = sysHelpService.findByCondition(1,total,SysHelpClassification.HELP, lang);
            JSONObject jsonSysHelp = new JSONObject();
            List<SysHelp> t1 = sysHelpPage.getContent();
            for(int i = 0; i < t1.size(); i++) {
            	t1.get(i).setContent(null);
            }
            jsonSysHelp.put("content", t1);
            jsonSysHelp.put("titleCN","新手指南");
            jsonSysHelp.put("titleEN","Getting Started");
            jsonSysHelp.put("cate","0");
            jsonResult.add(jsonSysHelp);

            //FAQ("常见问题")
            Page<SysHelp> sysFaqPage = sysHelpService.findByCondition(1,total,SysHelpClassification.FAQ, lang);
            JSONObject jsonSysFaq = new JSONObject();
            List<SysHelp> t2 = sysFaqPage.getContent();
            for(int i = 0; i < t2.size(); i++) {
            	t2.get(i).setContent(null);
            }
            jsonSysFaq.put("content", t2);
            jsonSysFaq.put("titleCN","常见问题");
            jsonSysFaq.put("titleEN","FAQ");
            jsonSysFaq.put("cate","1");
            jsonResult.add(jsonSysFaq);
            
            //Exchange("交易指南"),
            Page<SysHelp> sysExchangePage = sysHelpService.findByCondition(1,total,SysHelpClassification.EXCHANGE, lang);
            JSONObject jsonSysExchange = new JSONObject();
            List<SysHelp> t3 = sysExchangePage.getContent();
            for(int i = 0; i < t3.size(); i++) {
            	t3.get(i).setContent(null);
            }
            jsonSysExchange.put("content", t3);
            jsonSysExchange.put("titleCN","交易指南");
            jsonSysExchange.put("titleEN","Trade Guide");
            jsonSysExchange.put("cate","2");
            jsonResult.add(jsonSysExchange);
            
            //CoinInfo("币种资料"),
            Page<SysHelp> sysCoinsPage = sysHelpService.findByCondition(1,total,SysHelpClassification.COININFO, lang);
            JSONObject jsonSysCoins = new JSONObject();
            List<SysHelp> t4 = sysCoinsPage.getContent();
            for(int i = 0; i < t4.size(); i++) {
            	t4.get(i).setContent(null);
            }
            jsonSysCoins.put("content", t4);
            jsonSysCoins.put("titleCN","币种资料");
            jsonSysCoins.put("titleEN","Coins");
            jsonSysCoins.put("cate","3");
            jsonResult.add(jsonSysCoins);
            
            //Analysis("行情技术"),
            Page<SysHelp> sysAnalysisPage = sysHelpService.findByCondition(1,total,SysHelpClassification.ANALYSIS, lang);
            JSONObject jsonSysAnalysis = new JSONObject();
            List<SysHelp> t5 = sysAnalysisPage.getContent();
            for(int i = 0; i < t5.size(); i++) {
            	t5.get(i).setContent(null);
            }
            jsonSysAnalysis.put("content", t5);
            jsonSysAnalysis.put("titleCN","行情技术");
            jsonSysAnalysis.put("titleEN","Analysis");
            jsonSysAnalysis.put("cate","4");
            jsonResult.add(jsonSysAnalysis);
            
            //Protocol("条款协议"),
            Page<SysHelp> sysProtocolPage = sysHelpService.findByCondition(1,total,SysHelpClassification.PROTOCOL, lang);
            JSONObject jsonSysProtocol = new JSONObject();
            List<SysHelp> t6 = sysProtocolPage.getContent();
            for(int i = 0; i < t6.size(); i++) {
            	t6.get(i).setContent(null);
            }
            jsonSysProtocol.put("content", t6);
            jsonSysProtocol.put("titleCN","条款协议");
            jsonSysProtocol.put("titleEN","Protocols");
            jsonSysProtocol.put("cate","5");
            jsonResult.add(jsonSysProtocol);
            
            //Other("其他"),
            Page<SysHelp> sysOtherPage = sysHelpService.findByCondition(1,total,SysHelpClassification.OTHER, lang);
            JSONObject jsonSysOther= new JSONObject();
            List<SysHelp> t7 = sysOtherPage.getContent();
            for(int i = 0; i < t7.size(); i++) {
            	t7.get(i).setContent(null);
            }
            jsonSysOther.put("content", t7);
            jsonSysOther.put("titleCN","其他");
            jsonSysOther.put("titleEN","Other");
            jsonSysOther.put("cate","6");
            jsonResult.add(jsonSysOther);
            
            valueOperations.set(SysConstant.SYS_HELP+lang,jsonResult,SysConstant.SYS_HELP_EXPIRE_TIME, TimeUnit.SECONDS);
            return success(jsonResult);
        }
    }

    /**
     * 获取该分类（二级页面）
     * @param pageNo
     * @param pageSize
     * @param cate
     * @return
     */
    @RequestMapping(value = "more/help/page",method = RequestMethod.POST)
    public MessageResult sysHelpCate(@RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                    @RequestParam(value = "cate")SysHelpClassification cate,
                                    @RequestParam(value = "lang",defaultValue = "CN")String lang){
    	// 首页缓存
    	if(pageNo.intValue() == 1) {
	        ValueOperations valueOperations = redisTemplate.opsForValue();
	        JSONObject result = (JSONObject) valueOperations.get(SysConstant.SYS_HELP_CATE+cate+lang);
	        if (result != null){
	            return success(result);
	        }else {
	            JSONObject jsonObject = new JSONObject();
	            Page<SysHelp> sysHelpPage = sysHelpService.findByCondition(pageNo,pageSize,cate,lang);
	            jsonObject.put("content",sysHelpPage.getContent());
	            jsonObject.put("totalPage",sysHelpPage.getTotalPages());
	            jsonObject.put("totalElements",sysHelpPage.getTotalElements());
	            valueOperations.set(SysConstant.SYS_HELP_CATE+cate+lang,jsonObject,SysConstant.SYS_HELP_CATE_EXPIRE_TIME, TimeUnit.SECONDS);
	            return success(jsonObject);
	        }
    	}else {
    		JSONObject jsonObject = new JSONObject();
            Page<SysHelp> sysHelpPage = sysHelpService.findByCondition(pageNo,pageSize,cate,lang);
            jsonObject.put("content",sysHelpPage.getContent());
            jsonObject.put("totalPage",sysHelpPage.getTotalPages());
            jsonObject.put("totalElements",sysHelpPage.getTotalElements());
            return success(jsonObject);
    	}
    }

    /**
     * 获取该分类的置顶文章
     * @param cate
     * @return
     */
    @RequestMapping(value = "more/help/page/top", method = RequestMethod.POST)
    public MessageResult sysHelpTop(@RequestParam(value = "cate")String cate,
    								@RequestParam(value = "lang", defaultValue = "CN")String lang){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<SysHelp> result = (List<SysHelp>) valueOperations.get(SysConstant.SYS_HELP_TOP+cate+lang);
        if ( result != null && !result.isEmpty()){
            return success(result);
        }else {
            List<SysHelp> sysHelps = sysHelpService.getgetCateTops(cate, lang);
            valueOperations.set(SysConstant.SYS_HELP_TOP+cate+lang,sysHelps,SysConstant.SYS_HELP_TOP_EXPIRE_TIME,TimeUnit.SECONDS);
            return success(sysHelps);
        }
    }


    /**
     * 系统帮助详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "more/help/detail",method = RequestMethod.POST)
    public MessageResult sysHelpDetail(@RequestParam(value = "id") Long id) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SysHelp result = (SysHelp) valueOperations.get(SysConstant.SYS_HELP_DETAIL+id);
        if (result != null){
            return success(result);
        }else {
            SysHelp sysHelp = sysHelpService.findOne(id);
            valueOperations.set(SysConstant.SYS_HELP_DETAIL+id,sysHelp,SysConstant.SYS_HELP_DETAIL_EXPIRE_TIME,TimeUnit.SECONDS);
            return success(sysHelp);
        }

    }

    /**
     * 系统帮助详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/system/help/{id}")
    public MessageResult sysHelp(@PathVariable(value = "id") Long id) {
        //List<SysHelp> list = sysHelpService.findBySysHelpClassification(sysHelpClassification);
        SysHelp sysHelp = sysHelpService.findOne(id);
        MessageResult result = MessageResult.success();
        result.setData(sysHelp);
        return result;
    }

    /**
     * 移动版本号
     *
     * @param platform 0:安卓 1:苹果
     * @return
     */
    @RequestMapping("/system/app/version/{id}")
    public MessageResult sysHelp(@PathVariable(value = "id") Platform platform) {

        AppRevision revision = appRevisionService.findRecentVersion(platform);
        if(revision != null){
            MessageResult result = MessageResult.success();
            result.setData(revision);
            return result;
        }
        else{
            return MessageResult.error("no update");
        }
    }

}
