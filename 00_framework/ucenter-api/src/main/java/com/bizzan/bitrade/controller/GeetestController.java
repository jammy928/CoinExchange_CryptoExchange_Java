package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.HttpUtil;
import com.bizzan.bitrade.config.GeetestConfig;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.system.GeetestLib;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author GS
 * @date 2018年02月23日
 */
@RestController
@Slf4j
public class GeetestController extends BaseController {

    @Autowired
    private GeetestLib gtSdk;
    @Value("${water.proof.app.id}")
    private  String appId;
    @Value("${water.proof.app.secret.key}")
    private  String appSecretKey ;
    private static final String url = "https://ssl.captcha.qq.com/ticket/verify";

    private static MultiThreadedHttpConnectionManager connectionManager = null;

    private static int connectionTimeOut = 15000;

    private static int socketTimeOut = 15000;

    private static int maxConnectionPerHost = 500;

    private static int maxTotalConnections = 500;

    private static HttpClient client;


    static {
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setConnectionTimeout(connectionTimeOut);
        connectionManager.getParams().setSoTimeout(socketTimeOut);
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
        connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
        client = new HttpClient(connectionManager);
    }

    @RequestMapping(value = "/start/captcha")
    public String startCaptcha(HttpServletRequest request) {
        String resStr = "{}";
        String userid = "spark";
        //自定义参数,可选择添加
        HashMap<String, String> param = new HashMap<String, String>();
        String ip = getRemoteIp(request);
        param.put("user_id", userid); //网站用户id
        param.put("client_type", "web"); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("ip_address", ip); //传输用户请求验证时所携带的IP
        //进行验证预处理
        int gtServerStatus = gtSdk.preProcess(param);
        //将服务器状态设置到session中
        request.getSession().setAttribute(gtSdk.gtServerStatusSessionKey, gtServerStatus);
        //将userid设置到session中
        request.getSession().setAttribute("userid", userid);
        resStr = gtSdk.getResponseStr();
        return resStr;
    }

    public  Boolean  watherProof( String ticket,  String randStr, String ip) throws Exception {
        String response = null;
        GetMethod getMethod = null;
        Boolean responseBool = false;
        try {
            log.info("watherProof>>>>>start>>>ip>>>" + ip);
            StringBuilder sb = new StringBuilder();
            sb.append(url).append("?aid=").append(appId)
                    .append("&AppSecretKey=").append(appSecretKey)
                    .append("&Ticket=").append(ticket).
                    append("&Randstr=").append(randStr).
                    append("&UserIP=").append(ip);
            getMethod = new GetMethod(sb.toString());
            int code = client.executeMethod(getMethod);
            if (code == 200) {
                response = getMethod.getResponseBodyAsString();
            } else {
                log.info("状态响应码为>>>>>>" + code);
            }
        } catch (HttpException e) {
            log.info("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
        } catch (IOException e) {
            log.error("发生网络异常", e);
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
                getMethod = null;
            }
        }
        log.info(">>>>>>>>发送校验结果响应为>>>>>>"+response);
        if(!StringUtils.isEmpty(response)){
            JSONObject responseJson = JSONObject.parseObject(response);
            String code = responseJson.getString("response");
            if("1".equals(code)){
                responseBool = true ;
            }
        }
        return  responseBool;
    }
}
