package com.bizzan.bitrade.controller.exchange;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.entity.InitPlate;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.service.InitPlateService;
import com.bizzan.bitrade.util.MessageResult;

@RestController
@RequestMapping("HTL_plate")
@Slf4j
public class HTLExchangeInitPlateController {

    @Autowired
    private ExchangeCoinService exchangeCoinService;
    @Autowired
    private InitPlateService initPlateService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequiresPermissions("exchange:htl-init-plate:query")
    @PostMapping("query")
    public MessageResult queryExchangeInitPlate()throws Exception{
        MessageResult mr =new MessageResult();
        try {

            InitPlate initPlate = initPlateService.findInitPlateBySymbol("HTL/ETH");
            mr.setCode(0);
            mr.setMessage("success");
            mr.setData(initPlate);
        }catch (Exception e){
            log.info(">>>>queryExchanegCoin Error",e);
            e.printStackTrace();
            throw new Exception(e);
        }
        return mr;

    }
    @RequiresPermissions("exchange:htl-init-plate:detail")
    @GetMapping("detail/{id}")
    public MessageResult queryDetailExchangeInitPlate(@PathVariable("id")long id)throws Exception{
        MessageResult mr = new MessageResult();
        try {
            mr.setData(initPlateService.findByInitPlateId(id));
            mr.setCode(0);
            mr.setMessage("success");
        }catch (Exception e){
            log.info(">>>>queryDetailExchangeInitPlate Error={}",e);
            e.printStackTrace();
            throw new Exception(e);
        }

        return mr;
    }

    /**
     * 删除信息
     * @param id
     * @return
     * @throws Exception
     */
    @RequiresPermissions("exchange:htl-init-plate:delete")
    @GetMapping("delete/{id}")
    public MessageResult deleteExchangeInitPlate(@PathVariable("id")long id)throws Exception{
        MessageResult mr = new MessageResult();
        try {
            InitPlate initPlate = initPlateService.findByInitPlateId(id);
            if(initPlate==null){
                mr.setCode(500);
                mr.setMessage("不存在该记录");
                return mr;
            }
            initPlateService.delete(id);
            ValueOperations valueOperations = redisTemplate.opsForValue();
            String key = SysConstant.EXCHANGE_INIT_PLATE_SYMBOL_KEY+initPlate.getSymbol();
            valueOperations.getOperations().delete(key);
            mr.setCode(0);
            mr.setMessage("success");
        }catch (Exception e){
            log.info(">>>>deleteExchangeInitPlate Error={}",e);
            e.printStackTrace();
            throw new Exception(e);
        }

        return  mr ;
    }
    /**
     * 修改信息
     * @param initPlate
     * @return
     * @throws Exception
     */
    @RequiresPermissions("exchange:htl-init-plate:update")
    @PostMapping("update")
    public MessageResult updateExchangeInitPlate(InitPlate initPlate)throws Exception{
        MessageResult mr = new MessageResult();
        try {
            if (checkInitPlateParams(initPlate, mr)){
                return mr;
            }
            if(initPlate.getId()==null){
                mr.setCode(500);
                mr.setMessage("不存在该记录");
                return mr;
            }
            if(initPlateService.findByInitPlateId(initPlate.getId())==null){
                mr.setCode(500);
                mr.setMessage("不存在该记录");
                return mr;
            }
            initPlateService.saveAndFlush(initPlate);
            ValueOperations valueOperations = redisTemplate.opsForValue();
            String key = SysConstant.EXCHANGE_INIT_PLATE_SYMBOL_KEY+initPlate.getSymbol();
            valueOperations.getOperations().delete(key);
            mr.setCode(0);
            mr.setMessage("修改成功");
        }catch (Exception e){
            log.info(".>>>updateInitPlate Error ={}",e);
            e.printStackTrace();
            throw new Exception(e);
        }

        return mr;
    }



    private boolean checkInitPlateParams(@RequestParam InitPlate initPlate, MessageResult mr) {
        if(StringUtils.isEmpty(initPlate.getRelativeTime())||
                StringUtils.isEmpty(initPlate.getInitPrice())||
                StringUtils.isEmpty(initPlate.getFinalPrice())||
                StringUtils.isEmpty(initPlate.getRelativeTime())||
                StringUtils.isEmpty(initPlate.getSymbol())){
            mr.setCode(500);
            mr.setMessage("参数非法，请核实参数");
            return true;
        }
        //校验参数
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(initPlate.getSymbol());
        if(exchangeCoin==null || exchangeCoin.getEnable()==2){
            mr.setCode(500);
            mr.setMessage("交易对非法，请核实");
            return true;
        }
        int interferenceFactor =Integer.parseInt(initPlate.getInterferenceFactor());
        if(interferenceFactor<0 || interferenceFactor>70){
            mr.setCode(500);
            mr.setMessage("干扰因子非法，请输入1-70之间的整数");
            return true;
        }
        double initPrice = Double.parseDouble(initPlate.getInitPrice());
        double finalPrice =Double.parseDouble(initPlate.getFinalPrice());
        if(initPrice==finalPrice){
            mr.setCode(500);
            mr.setMessage("价格差值为0，请重新调整");
            return true;
        }
        return false;
    }



}
