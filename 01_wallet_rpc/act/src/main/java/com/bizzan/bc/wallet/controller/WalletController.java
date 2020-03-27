package com.bizzan.bc.wallet.controller;

import com.bizzan.bc.wallet.component.ActClient;
import com.bizzan.bc.wallet.util.MessageResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/rpc")
public class WalletController {
    @Autowired
    private ActClient rpcClient;
    private Logger logger = LoggerFactory.getLogger(WalletController.class);
    @Value("${coin.master-address:}")
    private String masterAddress;

    @GetMapping("height")
    public MessageResult getHeight(){
        try {
            Long height =rpcClient.getBlockCount();
            MessageResult result = new MessageResult(0,"success");
            result.setData(height - 1);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            return MessageResult.error(500,"查询失败,error:"+e.getMessage());
        }
    }


    @RequestMapping("address/{account}")
    public MessageResult getNewAddress(@PathVariable String account){
        logger.info("create new address :"+account);
        String subaddress=UUID.randomUUID().toString().replace("-","");
        String address = masterAddress +  subaddress;
        MessageResult result = new MessageResult(0,"success");
        result.setData(address);
        return result;
    }

}
