package com.bizzan.bitrade.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.entity.Order;
import com.bizzan.bitrade.service.OrderService;

import java.util.List;

/**
 * @author GS
 * @date 2018年01月22日
 */
@Component
@Slf4j
public class CheckOrderTask {
    @Autowired
    private OrderService orderService;

    @Scheduled(fixedRate = 60000)
    public void checkExpireOrder() {
        log.info("=========开始检查过期订单===========");
        List<Order> list = orderService.checkExpiredOrder();
        list.stream().forEach(x -> {
                    try {
                        orderService.cancelOrderTask(x);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.warn("订单编号{}:自动取消失败", x.getOrderSn());
                    }
                }
        );
        log.info("=========检查过期订单结束===========");
    }
}
