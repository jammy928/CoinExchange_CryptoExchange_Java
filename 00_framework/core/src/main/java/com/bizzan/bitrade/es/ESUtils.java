package com.bizzan.bitrade.es;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.config.ESConfig;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ESUtils {

    @Autowired
    private ESConfig esConfig;

    @Autowired
    private ESClient esClient;

    /**
     * 添加数据到Elasticsearch
     * @param param  一个对象json
     * @return
     */
    public  boolean save(JSONObject param) {


        String endPoint = esConfig.getEsMineIndex()+"/"+esConfig.getEsMineType();
        log.info("====endPoint====="+endPoint);
        JSONObject jsonObject =esClient.getClient("POST",endPoint,param);
        log.info("=======ES存储结果:"+jsonObject);
        if (jsonObject != null){
            return true;
        }
        return false;
    }

    /**
     * 从Elasticsearch查询数据
     * @param param
     * @return
     */
    public JSONObject query (JSONObject param){

        String endPoint = esConfig.getEsMineIndex()+"/"+esConfig.getEsMineType()+"/"+"_search";
        JSONObject jsonObject = esClient.getClient("POST",endPoint,param);
        log.info("=======ES查询结果:"+jsonObject);
        if (jsonObject != null){
            return jsonObject;
        }
        return null;
    }

    /**
     * 存储任意 es
     * @param param 参数
     * @param esIndex 库名
     * @param esType 表名
     * @return
     */
    public  boolean saveForAnyOne(JSONObject param ,String esIndex,String esType){
        String endPoint = esIndex+"/"+esType;
        log.info(">>>>>>endPoint>>>>>"+endPoint);
        JSONObject jsonObject =esClient.getClient("POST",endPoint,param);
        log.info("=======ES存储结果:"+jsonObject);
        if (jsonObject != null){
            return true;
        }
        return false;
    }


    /**
     * 查询任意es
     * @param param 入参
     * @param esIndex es库名
     * @param esType es表名
     * @return
     */
    public JSONObject queryForAnyOne(JSONObject param,String esIndex,String esType){
        String endPoint = esIndex+"/"+esType+"/_search";
        JSONObject jsonObject = esClient.getClient("POST",endPoint,param);
        if (jsonObject != null){
            return jsonObject;
        }
        return null;
    }

    public boolean saveMemberTransaction(MemberTransaction memberTransaction,String esIndex,String esType){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",memberTransaction.getId());
        jsonObject.put("address",memberTransaction.getAddress());
        jsonObject.put("amount",memberTransaction.getAmount());
        jsonObject.put("create_time",DateUtil.dateToString(memberTransaction.getCreateTime()));
        jsonObject.put("fee",memberTransaction.getFee());
        jsonObject.put("flag",memberTransaction.getFlag());
        jsonObject.put("member_id",memberTransaction.getMemberId());
        jsonObject.put("symbol",memberTransaction.getSymbol());
        jsonObject.put("type",TransactionType.parseOrdinal(memberTransaction.getType()));
        jsonObject.put("real_fee",new BigDecimal(memberTransaction.getRealFee()));
        jsonObject.put("discount_fee",new BigDecimal(memberTransaction.getDiscountFee()));
        log.info("===存入ES 数据==="+jsonObject);
        boolean result = saveForAnyOne(jsonObject,esIndex,esType);
        if (result == true){
            return true;
        }
        return false;
    }


}