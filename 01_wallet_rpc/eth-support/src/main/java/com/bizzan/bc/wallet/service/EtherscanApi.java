package com.bizzan.bc.wallet.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EtherscanApi {
    private Logger logger = LoggerFactory.getLogger(EtherscanApi.class);
    private String token;

    public void sendRawTransaction(String hexValue){
        try {
            HttpResponse<String> response =  Unirest.post("https://api.etherscan.io/api")
                    .field("module","proxy")
                    .field("action","eth_sendRawTransaction")
                    .field("hex",hexValue)
                    .field("apikey",token)
                    .asString();
            logger.info("sendRawTransaction result = {}",response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }


    public boolean checkEventLog(final Long blockHeight,String address,String topic0,String txid){
        try {
            HttpResponse<String> response = Unirest.post("https://api.etherscan.io/api")
                    .field("module", "logs")
                    .field("action", "getLogs")
                    .field("fromBlock", blockHeight)
                    .field("toBlock",blockHeight)
                    .field("address",address)
                    .field("topic0",topic0)
                    .field("apikey", token)
                    .asString();
            logger.info("getLogs result = {}",response.getBody());
            JSONObject result = JSON.parseObject(response.getBody());
            if(result.getInteger("status")==0){
                return false;
            }
            else{
                JSONArray txs = result.getJSONArray("result");
                for(int i=0;i<txs.size();i++){
                    JSONObject item = txs.getJSONObject(i);
                    if(item.getString("transactionHash").equalsIgnoreCase(txid))return true;
                }
                return false;
            }

        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args){
        EtherscanApi api = new EtherscanApi();
        //api.sendRawTransaction("0xf86e0585012a05f200830f4240950db4a46649c041b506e5d4965b8ed4f682f75b18ff8801c6fc1379856000801ca08e5e25623e588079f4fd795b48f34f128a07b63dc7385ca7d533671014417a11a00d093b1512b40265daf5db6bf3762188490a8a8d812a4756b599378e0d42855e");
        String txid = "0x4d95cdb7864f4aab4a349dbd2e3f8b9db1deb0f85f85d9a8c37a677958129c97";
        boolean ret = api.checkEventLog(6030689L,"0x0b42c73446e4090a7c1db8ac00ad46a38ccbc2ac","0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",txid);
        System.out.println(ret);
    }
}
