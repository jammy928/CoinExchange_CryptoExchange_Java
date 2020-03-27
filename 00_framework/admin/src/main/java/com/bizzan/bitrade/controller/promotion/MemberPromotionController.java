package com.bizzan.bitrade.controller.promotion;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.PromotionRewardType;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.QMember;
import com.bizzan.bitrade.entity.RewardPromotionSetting;
import com.bizzan.bitrade.model.MemberPromotionScreen;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberPromotionService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.RewardPromotionSettingService;
import com.bizzan.bitrade.util.BigDecimalUtils;
import com.bizzan.bitrade.util.ExcelUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.bizzan.bitrade.vo.PromotionMemberVO;
import com.bizzan.bitrade.vo.RegisterPromotionVO;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 邀请注册奖励配置
 * @author shaox
 *
 */
@RestController
@RequestMapping("promotion/member")
public class MemberPromotionController {

    @Autowired
    private MemberService memberService ;

    @Autowired
    private MemberPromotionService memberPromotionService ;

    @Autowired
    private RewardPromotionSettingService rewardPromotionSettingService ;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @PostMapping("page-query")
    @RequiresPermissions("promotion:member:page-query")
    @AccessLog(module = AdminModule.PROMOTION,operation = "推荐会员分页查询")
    public MessageResult page(PageModel pageModel, MemberPromotionScreen screen){
        Map<String,Object> map = getMemberPromotions(pageModel,screen);
        PageImpl<Object> pageImpl = new PageImpl<>((List<Object>)map.get("list"),pageModel.getPageable(),(long)map.get("total"));
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"),pageImpl);
    }


    @RequiresPermissions("promotion:member:details")
    @PostMapping("details")
    @AccessLog(module = AdminModule.PROMOTION,operation = "推荐会员明细")
    public MessageResult promotionDetails(PageModel pageModel,
                                          @RequestParam("memberId")Long memberId){
        pageModel.setSort();
        Page<RegisterPromotionVO> page = memberPromotionService.getPromotionDetails(memberId,pageModel);
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"),page);
    }

    @RequiresPermissions("promotion:member:out-excel")
    @GetMapping("out-excel")
    public void outExcel(PageModel pageModel, MemberPromotionScreen screen,
                         HttpServletResponse response) throws Exception {
        Map<String,Object> map = getMemberPromotions(pageModel,screen);
        ExcelUtil.listToExcel((List<PromotionMemberVO>)map.get("list"),PromotionMemberVO.class.getDeclaredFields(),response.getOutputStream());
    }

    private Map getMemberPromotions(PageModel pageModel, MemberPromotionScreen screen){
        List<BooleanExpression> booleanExpressions = new ArrayList<>() ;
        if(!StringUtils.isEmpty(screen.getAccount())) {
            booleanExpressions.add(QMember.member.username.like("%"+screen.getAccount()+"%")
                    .or(QMember.member.realName.like("%"+screen.getAccount()+"%"))
                    .or(QMember.member.mobilePhone.like(screen.getAccount()+"%"))
                    .or(QMember.member.email.like(screen.getAccount()+"%")));
        }
        if(screen.getMinPromotionNum()!=-1) {
            booleanExpressions.add(QMember.member.firstLevel.add(QMember.member.secondLevel).goe(screen.getMinPromotionNum()));
        }

        if(screen.getMaxPromotionNum()!=-1) {
            booleanExpressions.add(QMember.member.firstLevel.add(QMember.member.secondLevel).loe(screen.getMaxPromotionNum()));
        }

        RewardPromotionSetting setting = rewardPromotionSettingService.findByType(PromotionRewardType.REGISTER);

        Assert.notNull(setting,"注册奖励配置 null");

        String info = setting.getInfo() ;

        JSONObject jsonObject = JSONObject.parseObject(info);

        BigDecimal one = jsonObject.getBigDecimal("one");

        BigDecimal two = jsonObject.getBigDecimal("two");

        Map<String,String> map = new HashMap<>();

        Page<Member> page = memberService.findAll(PredicateUtils.getPredicate(booleanExpressions),pageModel.getPageable());

        List<PromotionMemberVO> list = page.getContent().stream().map(x -> PromotionMemberVO.builder().id(x.getId())
                .username(x.getUsername())
                .email(x.getEmail())
                .mobilePhone(x.getMobilePhone())
                .realName(x.getRealName())
                .promotionCode(x.getPromotionCode())
                .promotionNum(x.getFirstLevel()+x.getSecondLevel())
                .reward(map.put(setting.getCoin().getName(),
                        BigDecimalUtils.mul(one,new BigDecimal(x.getFirstLevel()+"").add(
                                BigDecimalUtils.mul(two,new BigDecimal(x.getSecondLevel()+""))
                        )).toString())!=null?map:null)
                .build())
                .collect(Collectors.toList());
        Map<String,Object> map1 = new HashMap();
        map1.put("total",page.getTotalElements());
        map1.put("list",list);
        return map1 ;
    }
}
