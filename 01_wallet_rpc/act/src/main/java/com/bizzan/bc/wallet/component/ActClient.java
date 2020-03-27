package com.bizzan.bc.wallet.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bc.wallet.entity.ActTransaction;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class ActClient extends JsonrpcClient{

    public ActClient(String url) throws URISyntaxException, MalformedURLException {
        super(url);
    }

    public Long getBlockCount() throws UnirestException {
        Object result = this.invoke("blockchain_get_block_count");
        return Long.parseLong(result.toString());
    }

    public JSONObject getBlock(Long height) throws UnirestException {
        Object result = this.invoke("blockchain_get_block",height);
        return JSON.parseObject(result.toString());
    }

    public ActTransaction getTransaction(String txid) throws UnirestException {
        Object result = this.invoke("blockchain_get_transaction",txid);
        JSONArray array =  JSON.parseArray(result.toString());

        JSONObject body = array.getJSONObject(1);
        if(body.getJSONObject("trx").getJSONObject("alp_inport_asset").getInteger("asset_id")==0){
            JSONArray ops = body.getJSONObject("trx").getJSONArray("operations");
            if(ops.getJSONObject(0).getString("type").equalsIgnoreCase("withdraw_op_type")
                    || ops.getJSONObject(0).getString("type").equalsIgnoreCase("deposit_op_type")) {
                JSONObject prettyTx = JSON.parseObject(invoke("blockchain_get_pretty_transaction",txid).toString());
                ActTransaction transaction = new ActTransaction();
                transaction.setTxid(txid);
                transaction.setFrom(prettyTx.getString("from_account"));
                transaction.setTo(prettyTx.getString("to_account"));
                transaction.setType(0);
                transaction.setAmount(new BigDecimal(prettyTx.getBigInteger("amount")).divide(new BigDecimal(100000),8,BigDecimal.ROUND_DOWN));
                return transaction;
            }
        }
        return null;
    }

    public BigDecimal getAddressBalance(String address) throws UnirestException {
        Object result = this.invoke("blockchain_list_address_balances",address);
        System.out.println(result.toString());
        return BigDecimal.ZERO;
    }
}
