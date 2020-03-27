package com.bizzan.bitrade.controller.system;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.QTransferAddress;
import com.bizzan.bitrade.entity.TransferAddress;
import com.bizzan.bitrade.model.screen.TransferAddressScreen;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.TransferAddressService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.dsl.BooleanExpression;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/system/transfer-address")
public class TransferAddressController {

    @Autowired
    private CoinService coinService ;

    @Autowired
    private TransferAddressService transferAddressService ;

    @RequiresPermissions("system:transfer-address:merge")
    @PostMapping("merge")
    public MessageResult merge(@Valid TransferAddress transferAddress , @RequestParam("coinName") String coinName){
        Coin coin = coinService.findOne(coinName);
        transferAddress.setCoin(coin);
        transferAddressService.save(transferAddress);
        return MessageResult.success("保存成功");
    }

    @RequiresPermissions("system:transfer-address:page-query")
    @PostMapping("page-query")
    public MessageResult pageQuery(PageModel pageModel, TransferAddressScreen transferAddressScreen){
        List<BooleanExpression> booleanExpressions = new ArrayList<>();
        if(StringUtils.isNotBlank(transferAddressScreen.getAddress())) {
            booleanExpressions.add(QTransferAddress.transferAddress.address.eq(transferAddressScreen.getAddress()));
        }
        if(StringUtils.isNotBlank(transferAddressScreen.getUnit())) {
            booleanExpressions.add(QTransferAddress.transferAddress.coin.unit.equalsIgnoreCase(transferAddressScreen.getAddress()));
        }
        if(transferAddressScreen.getStart()!=null) {
            booleanExpressions.add(QTransferAddress.transferAddress.status.eq(transferAddressScreen.getStart()));
        }
        Page<TransferAddress> page = transferAddressService.findAll(PredicateUtils.getPredicate(booleanExpressions),pageModel);
        return MessageResult.getSuccessInstance("获取成功",page);
    }

    @RequiresPermissions("system:transfer-address:detail")
    @PostMapping("detail")
    public MessageResult detail(Long id){
        TransferAddress transferAddress = transferAddressService.findById(id);
        return MessageResult.getSuccessInstance("获取成功",transferAddress);
    }

    @RequiresPermissions("system:transfer-address:deletes")
    @PostMapping("deletes")
    public MessageResult deletes(Long[] ids){
        transferAddressService.deletes(ids);
        return MessageResult.success("删除成功");
    }

}
