package com.bizzan.bitrade.job;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.coin.CoinExchangeFactory;
import com.bizzan.bitrade.entity.OtcCoin;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.service.AdvertiseService;
import com.bizzan.bitrade.service.OtcCoinService;
import com.sparkframework.sql.DataException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author GS
 * @date 2018年02月01日
 */
@Component
@Slf4j
public class CheckAdvertiseTask {
    @Autowired
    private CoinExchangeFactory coins;
    @Autowired
    private OtcCoinService otcCoinService;
    @Autowired
    private AdvertiseService advertiseService;

    @Scheduled(fixedRate = 60000 * 30)
    public void checkExpireOrder() {
        log.info("=========开始检查自动下架的广告===========");
        //支持的币种
        List<OtcCoin> list = otcCoinService.getNormalCoin();
        Map<String, BigDecimal> map = coins.getCoins();
        list.stream().forEach(
                x -> {
                    BigDecimal marketPrice = map.get(x.getUnit());
                    try {
                        List<Map<String, String>> list1 = advertiseService.selectSellAutoOffShelves(x.getId(), marketPrice, x.getJyRate());
                        List<Map<String, String>> list2 = advertiseService.selectBuyAutoOffShelves(x.getId(), marketPrice);
                        list1.addAll(list2);
                        list1.stream().forEach(
                                y -> {
                                    try {
                                        advertiseService.autoPutOffShelves(y, x);
                                    } catch (InformationExpiredException e) {
                                        e.printStackTrace();
                                        log.warn("{}号广告:自动下架失败", y.get("id"));
                                    }
                                }
                        );
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (DataException e) {
                        e.printStackTrace();
                    }
                }
        );
        log.info("=========结束检查自动下架的广告===========");
    }
}
