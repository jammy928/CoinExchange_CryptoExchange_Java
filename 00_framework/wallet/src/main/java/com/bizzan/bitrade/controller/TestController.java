package com.bizzan.bitrade.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.util.MessageResult;

import java.util.LinkedHashMap;
import java.util.List;


@RestController
@RequestMapping("test")
public class TestController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CoinService coinService;

    @RequestMapping("/height/{unit}")
    public MessageResult test(@PathVariable("unit") String unit) {
        String serviceName = "SERVICE-RPC-" + unit.toUpperCase();
        String url = "http://" + serviceName + "/rpc/height";
        ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class);
        System.out.print("remote call:service={},result={}" + serviceName + result);
        return success(result);
    }

    /***
     * 获取 所有币种rpc 是否正常
     * GS
     * @return
     */
    @RequestMapping("/rpc")
    public MessageResult test() {
        logger.info("获取 所有币种rpc 是否正常");
        //rpc coin
        List<String> units = coinService.findAllRpcUnit();
        if (units == null || units.size() == 0) {
            return error("no rpc coin!");
        }
        logger.info("units = {}", units);
        //结果
        LinkedHashMap<String, ResponseEntity<MessageResult>> data = new LinkedHashMap<>(units.size());
        units.stream().forEach(
                x -> {
                    String serviceName = "SERVICE-RPC-" + x.toUpperCase();
                    String url = "http://" + serviceName + "/rpc/height";
                    ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class);
                    data.put(url, result);
                }
        );
        return success(data);
    }


}
