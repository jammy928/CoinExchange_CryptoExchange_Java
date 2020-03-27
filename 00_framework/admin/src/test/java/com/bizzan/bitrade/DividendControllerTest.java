package com.bizzan.bitrade;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bizzan.bitrade.WebApplication;
import com.bizzan.bitrade.service.OrderDetailAggregationService;
import com.bizzan.bitrade.util.DateUtil;

/**
 * @author Shaoxianjun
 * @date 2019年03月22日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=WebApplication.class)
public class DividendControllerTest {
    @Autowired
    private OrderDetailAggregationService orderDetailAggregationService;
    @Test
    public void queryStatistics(){
        long start = DateUtil.strToDate("2018-03-01 12:30:30").getTime();
        long end = DateUtil.strToDate("2018-03-22 14:30:30").getTime();
        System.out.println("start:"+start+"-----end:"+end);
        orderDetailAggregationService.queryStatistics(start,end);
    }
}
