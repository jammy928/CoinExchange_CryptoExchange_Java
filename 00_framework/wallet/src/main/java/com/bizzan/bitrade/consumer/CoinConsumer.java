package com.bizzan.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GS
 * @date 2018年02月08日
 */
@Component
public class CoinConsumer {

    private Logger logger = LoggerFactory.getLogger(CoinConsumer.class);
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MemberService memberService;
    @PersistenceContext
    protected EntityManager em;

    /**
     * 添加新币种，为用户增加钱包
     *
     * @param content
     */
    @KafkaListener(topics = {"coin-start"})
    public void handle(String content) {
        logger.info("handle coin-start,data={}", content);
        if (StringUtils.isEmpty(content)) {
            return;
        }
        JSONObject json = JSON.parseObject(content);
        if (json == null) {
            return;
        }
        Coin coin = coinService.findOne(json.getString("name"));
        if (coin != null && coin.getEnableRpc().equals(BooleanEnum.IS_TRUE)) {
            List<Member> list = memberService.findAll();
            int size = list.size();
            List<String> list1 = new ArrayList<>();
            List<Map> mapList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Member member = list.get(i);
                list1.add("U" + member.getId());
                if (i % 1000 == 0 || i == size - 1) {
                    //远程RPC服务URL,后缀为币种单位
                    String serviceName = "SERVICE-RPC-" + coin.getUnit();
                    String url = "http://" + serviceName + "/rpc/address/batch";
                    ResponseEntity<List> result = restTemplate.postForEntity(url, list1, List.class);
                    logger.info("remote call:service={},result={}", serviceName, result);
                    if (result.getStatusCode().value() == 200) {
                        mapList.addAll(result.getBody());
                    }
                    list1.clear();
                }
            }
            int mapListSize = mapList.size();
            for (int i = 0; i < mapListSize; i++) {
                MemberWallet wallet = new MemberWallet();
                wallet.setCoin(coin);
                wallet.setMemberId((Long) mapList.get(i).get("uid"));
                wallet.setBalance(new BigDecimal(0));
                wallet.setFrozenBalance(new BigDecimal(0));
                wallet.setAddress(mapList.get(i).get("address").toString());
                em.persist(wallet);
                if (i % 1000 == 0 || i == size - 1) {
                    em.flush();
                    em.clear();
                }
            }
            em.flush();
            em.clear();
        }
    }
}
