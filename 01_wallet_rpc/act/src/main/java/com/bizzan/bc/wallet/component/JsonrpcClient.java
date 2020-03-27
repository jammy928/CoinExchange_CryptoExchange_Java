package com.bizzan.bc.wallet.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.utils.Base64Coder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class JsonrpcClient {
    private String url;
    private Map<String,String> headers = new HashMap<>();

    public JsonrpcClient(String url) throws URISyntaxException, MalformedURLException {
        this(new URL(url));
    }

    public JsonrpcClient(URL rpc) throws URISyntaxException, MalformedURLException {
        this.url = new URI(rpc.getProtocol(), null, rpc.getHost(), rpc.getPort(), rpc.getPath(), rpc.getQuery(), null).toString();
        if(StringUtils.isNotEmpty(rpc.getUserInfo())){
            headers.put("Authorization", "Basic " + Base64Coder.encodeString(rpc.getUserInfo()));
        }
    }

    public Object invoke(String method, Object ... args) throws UnirestException {
        JSONObject request = new JSONObject();
        request.put("id","1");
        request.put("jsonrpc","2.0");
        request.put("method",method);
        JSONArray params = new JSONArray();
        for(int i=0;i<args.length;i++){
            params.add(args[i]);
        }
        request.put("params",params);
        HttpResponse<String> result =  Unirest.post(url)
                .headers(headers)
                .body(request.toJSONString())
                .asString();
        log.info(result.getBody());
        if(result.getStatus() != 200){
            throw new RuntimeException("访问RPC服务失败");
        }
        String raw = result.getBody();
        return JSON.parseObject(raw).get("result");
    }
}
