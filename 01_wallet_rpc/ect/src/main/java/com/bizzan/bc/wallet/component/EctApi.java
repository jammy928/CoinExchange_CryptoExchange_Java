package com.bizzan.bc.wallet.component;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EctApi {
    @Value("${coin.rpc}")
    private String host = "http://52.80.243.177:5990/v1";

    /**
     * 获取当前最新高度
     * @return
     */
    public Long getLatestHeight(){
        try {
            HttpResponse<String> response = Unirest.get(host + "/server")
                    .asString();
            JSONObject result = JSON.parseObject(response.getBody());
            if(result.getBoolean("success")){
                String completeLedgers = result.getJSONObject("sdchaind_server_status").getString("complete_ledgers");
                return Long.parseLong(completeLedgers.split("-")[1]);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return -1L;
        }
        return -1L;
    }

    /**
     * 获取交易
     * @param address
     * @param startHeight
     * @param endHeight
     * @param currency
     * @return
     */
    public JSONArray getTrasactions(String address,Long startHeight,Long endHeight,String currency){
        try {
            HttpResponse<String> response = Unirest.get(host + "/accounts/payments/"+address)
                    .queryString("destination_account",address)
                    .queryString("start_ledger",startHeight)
                    .queryString("end_ledger",endHeight)
                    .asString();
            JSONObject result = JSON.parseObject(response.getBody());
            if(result.getBoolean("success")){
                JSONArray txs =  result.getJSONArray("payments");
                JSONArray array = new JSONArray();
                for(int index=0;index<txs.size();index++){
                    if(txs.getJSONObject(index).getJSONObject("amount").getString("currency").equalsIgnoreCase(currency)){
                        array.add(txs.getJSONObject(index));
                    }
                }
                return array;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void getNewWallet(){
        try{
            HttpResponse<String> response = Unirest.get(host + "/wallet/new")
                    .asString();
            System.out.println(response.getBody());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public BigDecimal getBalance(String address,String currency){
        try{
            HttpResponse<String> response = Unirest.get(host + "/accounts/balances/"+address)
                    .asString();
            System.out.println(response.getBody());
            JSONObject result = JSON.parseObject(response.getBody());
            if(result.getBoolean("success")){
                JSONArray balances = result.getJSONArray("balances");
                for(int i=0,size=balances.size();i<size;i++){
                    JSONObject item = balances.getJSONObject(i);
                    if(item.getString("currency").equalsIgnoreCase(currency)){
                        return new BigDecimal(item.getString("value"));
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }


    /**
     * 发送交易
     * @param privatekey
     * @param from
     * @param to
     * @param amount
     * @return
     */
    public String sendFrom(String privatekey, String from, String to, BigDecimal amount,String remark){
        try{
            JSONObject request = new JSONObject();
            request.put("secret",privatekey);
            JSONObject payment = new JSONObject();
            payment.put("source_account",from);
            payment.put("destination_account",to);
            payment.put("amount",amount.toPlainString());
            JSONArray memos = new JSONArray();
            JSONObject memo = new JSONObject();
            memo.put("memo_type","UID");
            memo.put("memo_data",remark);
            memos.add(memo);
            payment.put("memos",memos);
            request.put("payment",payment);
            System.out.println(request.toJSONString());
            HttpResponse<String> response = Unirest.post(host + "/accounts/payments/"+from+"?submit=true")
                    .header("Content-Type","application/json")
                    .body(request.toJSONString())
                    .asString();
            System.out.println(response.getBody());
            JSONObject result = JSON.parseObject(response.getBody());
            if(result.getBoolean("success")){
                return result.getString("hash");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        EctApi api = new EctApi();
        String txid = api.sendFrom("snDPJkVBdJW733t2BY1LnsjfHxSad","eneiBsnNar7kw4MFYYfTTRAoucaaTPdmxQ",
                "esV75BQfiEiKdgaivjEYCt7EXk3BwJiscX",new BigDecimal("10"),"12");
        System.out.println(txid);
    }
}
