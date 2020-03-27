package com.bizzan.bitrade.controller.exchange;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.util.MessageResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/***
 * 交易挖矿明细
 * @auther: Hou RuiPeng
 * @Date: 2018/8/30
 */
@RestController
@RequestMapping("/exchange/exchange-order-Mine-Detail")
@Slf4j
public class ExchangeOrderMineDetailController extends BaseAdminController{

    @Autowired
    private ESUtils esUtils;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ExchangeCoinService coinService;

    /**
     * 获取支持的交易币种
     * @return
     */
    @RequestMapping(value = "/symbol",method = RequestMethod.GET)
    public MessageResult findAllSymbol(){
        List<ExchangeCoin> coins = coinService.findAllEnabled();
        return success(coins);
    }

    /**
     * 根据es查询交易挖矿数据，条件查询，memberId or phone or time
     * @param memberId
     * @param phone
     * @param exchangeCoin
     * @param startTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */

    @RequestMapping(value = "/list-es-Page",method = RequestMethod.POST)
    public MessageResult getExchangeOrderMineListByEs(@RequestParam(value = "memberId",required = false)Long memberId,
                                                      @RequestParam(value = "phone",required = false)String phone,
                                                      @RequestParam(value = "exchangeCoin",required = false)String exchangeCoin,
                                                      @RequestParam(value = "startTime",required = false)String startTime,
                                                      @RequestParam(value = "endTime",required = false)String endTime,
                                                      @RequestParam(value = "page") int page,
                                                      @RequestParam(value = "pageSize") int pageSize){

        try {
            log.info(">>>>>>>>>>>>>查询订单列表 开始>>>>>>>>>>>>>");
            //组装查询json
            StringBuffer query=new StringBuffer("{\"from\":" + (page - 1) * pageSize + ",\"query\":{\"bool\":{\"must\":[");
            if (memberId!=null) {
                //匹配用户id
                query.append("{\"match\":{\"member_id\":\"" + memberId + "\"}}");
            }
            if (!StringUtils.isEmpty(phone)) {
                //通过手机号获取用户id
                Member member=memberService.findByPhone(phone);
                query.append("{\"match\":{\"member_id\":\"" + member.getId() + "\"}}");
            }
            if (!StringUtils.isEmpty(exchangeCoin)) {
                //匹配交易对
                query.append("{\"match\":{\"symbol\":\"" + exchangeCoin + "\"}}");
            }
            if(!StringUtils.isEmpty(startTime) &&!StringUtils.isEmpty(endTime)){
                //时间范围语句
                query.append("{\"range\":{\"transaction_time\":{\"gte\":\""+startTime+"\",\"lte\":\""+endTime+"\"}}}");
            }
            query.append("]}},\"size\":" + pageSize + ",\"sort\":[{\"transaction_time\":{\"order\":\"desc\"}}]}");

            return success(esUtils.query(JSONObject.parseObject(query.toString())));

        }catch (Exception e){
            log.info(">>>>>>>>查询订单列表失败>>>>>>",e);
            return MessageResult.error("查询订单列表失败");
        }

    }

}
