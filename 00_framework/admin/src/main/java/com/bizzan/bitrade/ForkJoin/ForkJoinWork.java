package com.bizzan.bitrade.ForkJoin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.MessageResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @Description:
 * @Author: GuoShuai
 * @Date: 2018/8/1 下午4:27
 */
@Slf4j
public class ForkJoinWork extends RecursiveTask<Integer> {

    @Autowired
    private CoinService coinService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MemberWalletService walletService;



    /**
     * 起始值
     */
    private int start;

    /**
     * 结束值
     */
    private int end;

    private List<Member> objectList;

    private String coinName;

    /**
     * 临界值
     */
    public static final  int critical = 10000;


    public ForkJoinWork(int start, int end,List<Member> objectList,String coinName) {
        this.start = start;
        this.end = end;
        this.objectList = objectList;
        this.coinName = coinName;
    }



    @Override
    protected Integer compute() {
        //判断是否是拆分完毕
        int lenth = end - start;
        if(lenth<=critical){
            Coin coin = coinService.findOne(coinName);
            if (coin == null) {
                return null;
            }
            objectList.forEach(member -> {
                MemberWallet wallet = memberWalletService.findByCoinAndMember(coin, member);
                if (wallet == null) {
                    wallet = new MemberWallet();
                    wallet.setCoin(coin);
                    wallet.setMemberId(member.getId());
                    wallet.setBalance(new BigDecimal(0));
                    wallet.setFrozenBalance(new BigDecimal(0));
                    if (coin.getEnableRpc() == BooleanEnum.IS_TRUE) {
                        String account = "U" + member.getId();
                        //远程RPC服务URL,后缀为币种单位
                        String serviceName = "SERVICE-RPC-" + coin.getUnit();
                        log.info("=====serviceName====="+serviceName);
                        try {
                            String url = "http://" + serviceName + "/rpc/address/{account}";
                            ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class, account);
                            log.info("remote call:service={},result={}", serviceName, result);
                            if (result.getStatusCode().value() == 200) {
                                MessageResult mr = result.getBody();
                                if (mr.getCode() == 0) {
                                    //返回地址成功，调用持久化
                                    String address = (String) mr.getData();
                                    log.info("=====address====="+address);
                                    wallet.setAddress(address);
                                }
                            }
                            Thread.sleep(10L);
                        } catch (Exception e) {
                            log.error("call {} failed,error={}", serviceName, e.getMessage());
                            wallet.setAddress("");
                        }
                    } else {
                        wallet.setAddress("");
                    }
                    walletService.save(wallet);
                }
            });

            return objectList.size();
        }else {
            //没有拆分完毕就开始拆分
            //计算的两个值的中间值
            int middle = (end + start)/2;
            ForkJoinWork right = new ForkJoinWork(start,middle,objectList,coinName);
            right.fork();//拆分，并压入线程队列
            ForkJoinWork left = new ForkJoinWork(middle+1,end,objectList,coinName);
            left.fork();//拆分，并压入线程队列

            //合并
            return right.join() + left.join();
        }
    }
}
