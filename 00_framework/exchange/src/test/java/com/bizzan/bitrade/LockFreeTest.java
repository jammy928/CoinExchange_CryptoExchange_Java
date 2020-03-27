/*
 * Copyright (c) 2017-2018 阿期米德 All Rights Reserved.
 * @Author: sanfeng
 * @Date: 2018/7/10 14:06
 * @Version: 1.0
 * History:
 * v1.0.0, sanfeng,  2018/7/10 14:06, Create
 */
package com.bizzan.bitrade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

/**
 * <p>Description: </p>
 *
 * @Author: sanfeng
 * @Date: 2018/7/10 14:06
 */
public class LockFreeTest {
    /**
     * 排序测试，分别产生100个已经排序数据，1000个已经排序数据，10000个已经排序数据，对比使用collection.sort方法和直接对比插入的效率
     */
    @Test
    public void testSort(){
        doSort(100);
        doSort(1000);
        doSort(10000);
//        doSort(100000);
//        doSort(500000);

    }
    public void doSort(int totalNum){
        LinkedList<Integer> ints = new LinkedList<>();
        for(int i=0;i<totalNum;i++){
            ints.add(i);
        }
        long startTime = System.currentTimeMillis();
        System.out.println("测试列表数据为"+totalNum+"时的排序对比，直接对比测试，开始时间："+startTime);
        Random random = new Random();
        for(int i=0;i<1000;i++){
            int n = random.nextInt(totalNum);
            int index=0;
            for(int j=0;j<totalNum;j++){
                if(n<ints.get(j)){
                    index = j;
                    break;
                }
            }
            ints.add(index,n);
        }
        System.out.println("普通排序"+totalNum+"测试完成，耗时："+(System.currentTimeMillis()-startTime));
        //System.out.println("排序后的结果为：");
        //System.err.println(ints);
        ints = new LinkedList<>();
        for(int i=0;i<totalNum;i++){
            ints.add(i);
        }
        startTime = System.currentTimeMillis();
        System.out.println("测试列表数据为"+totalNum+"时的排序对比，快排对比测试，开始时间："+startTime);
        for(int i=0;i<1000;i++){
            int n = random.nextInt(totalNum);
            ints.add(n);
            ints.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1-o2;
                }
            });
        }
        System.out.println("快速排序"+totalNum+"测试完成，耗时："+(System.currentTimeMillis()-startTime));
        //System.out.println("排序后的结果为：");
        //System.err.println(ints);
    }
    @Test
    public void testTrade(){
        Random random = new Random();
        int basePrice=40000;
        WaitBean waitBean = new WaitBean(1000);
        for(int i=0;i<1;i++){//模仿1000用户下单
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            long insertTime = System.currentTimeMillis();
                            TradeType tradeType = random.nextInt(9) <5 ? TradeType.LIMIT : TradeType.MARKET;
                            TradeDirection tradeDirection = random.nextInt(9) <3 ? TradeDirection.SELL : TradeDirection.BUY;
                            Order order = new Order(
                                    tradeType, tradeDirection, System.currentTimeMillis() + random.nextInt(basePrice), basePrice + random.nextInt(10000), insertTime);

                            waitBean.trade(order);
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    long tradeNum =  waitBean.getFinishOrder().size()-waitBean.getPreTotalTrade();
                    System.out.println("当前5ms成交量为:"+tradeNum+",系统吞吐量为:"+tradeNum/5+"单/s,限价买入队列长度："+waitBean.getBuyLimitPriceOrderList().size()
                            +"，限价卖出队列长度:"+waitBean.getSellLimitPriceOrderList().size()+"，市价买入队列长度:"+waitBean.getBuyMarketPriceList().size()+"，市价卖出队列长度："+
                            waitBean.getSellMarketPriceList().size());
                    waitBean.setPreTotalTrade(waitBean.getFinishOrder().size());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
        String readline;
        try {
            readline=sin.readLine(); //从系统标准输入读入一字符串
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class TestComparator implements  Comparator<Order>{
    @Override
    public int compare(Order o1, Order o2) {
        return o1.getPrice()-o2.getPrice();
    }
}
class TestComparator2 implements  Comparator<Order>{
    @Override
    public int compare(Order o1, Order o2) {
        return o2.getPrice()-o1.getPrice();
    }
}
class WaitBean{
    //买入限价订单链表，价格从高到低排列
    private LinkedList<Order> buyLimitPriceOrderList = new LinkedList<>();
    //卖出限价订单链表，价格从低到高排列
    private LinkedList<Order> sellLimitPriceOrderList = new LinkedList<>();
    //买入市价订单链表，按时间从小到大排序
    private LinkedList<Order> buyMarketPriceList = new LinkedList<>();
    //卖出市价订单链表，按时间从小到大排序
    private LinkedList<Order> sellMarketPriceList = new LinkedList<>();
    private LinkedList<Order> finishOrder = new LinkedList<>();

    public LinkedList<Order> getBuyLimitPriceOrderList() {
        return buyLimitPriceOrderList;
    }

    public LinkedList<Order> getSellLimitPriceOrderList() {
        return sellLimitPriceOrderList;
    }

    public LinkedList<Order> getBuyMarketPriceList() {
        return buyMarketPriceList;
    }

    public LinkedList<Order> getSellMarketPriceList() {
        return sellMarketPriceList;
    }

    public LinkedList<Order> getFinishOrder() {
        return finishOrder;
    }

    public long getPreTotalTrade() {
        return preTotalTrade;
    }

    public void setPreTotalTrade(long preTotalTrade) {
        this.preTotalTrade = preTotalTrade;
    }

    public void setFinishOrder(LinkedList<Order> finishOrder) {
        this.finishOrder = finishOrder;
    }

    long preTotalTrade=0;
    Comparator comparatorBuy = new TestComparator2();
    Comparator comparatorSell = new TestComparator();
    Random random = new Random();

    public WaitBean(int num){

        int basePrice=40000;

        for(int i=0;i<num;i++){
            buyLimitPriceOrderList.add(new Order(
                    TradeType.LIMIT,TradeDirection.BUY,System.currentTimeMillis()+random.nextInt(basePrice),basePrice+random.nextInt(10000),System.currentTimeMillis()));
            sellLimitPriceOrderList.add(new Order(
                    TradeType.LIMIT,TradeDirection.SELL,System.currentTimeMillis()+random.nextInt(basePrice),basePrice+random.nextInt(10000),System.currentTimeMillis()));
            buyMarketPriceList.add(new Order(
                    TradeType.MARKET,TradeDirection.BUY,System.currentTimeMillis()+random.nextInt(basePrice),basePrice+random.nextInt(10000),System.currentTimeMillis()));
            sellMarketPriceList.add(new Order(
                    TradeType.MARKET,TradeDirection.SELL,System.currentTimeMillis()+random.nextInt(basePrice),basePrice+random.nextInt(10000),System.currentTimeMillis()));

        }
        buyLimitPriceOrderList.sort(comparatorBuy);
        sellLimitPriceOrderList.sort(comparatorSell);
//        System.err.println(buyLimitPriceOrderList);
//        System.err.println(sellLimitPriceOrderList);

    }

    /**
     *
     * @param type 交易类型：1-市价 2-限价
     * @param direction  交易方向：1-买 2-卖
     * @param price
     */
    public void trade(Order order){
        if(order.getTradeDirection()==TradeDirection.BUY){//买入
            if(order.getTradeType()==TradeType.MARKET){//市价消费
                marketBuyDeal(order);
            }else{//限价消费
                limitBuyDeal(order);
            }
        }else{//卖出
            if(order.getTradeType()==TradeType.MARKET){//市价消费
                marketSellDeal(order);
            }else{//限价消费
                limitSellDeal(order);
            }
        }
    }

    /**
     * 市价买入
     * @param price
     */
    private  boolean marketBuyDeal(Order order){
        boolean match=false;
        if(!CollectionUtils.isEmpty(sellLimitPriceOrderList)){//限价卖出委托不为空
            Iterator<Order> iterator = sellLimitPriceOrderList.iterator();

            while(iterator.hasNext()){//可以不用循环，防止多手场景
                Order iterOrder =iterator.next();
                iterator.remove();
                finishOrder(order,iterOrder);
                match=true;
                break;
            }
            if(!match){//没有匹配到，加入市价买入队列
                buyMarketPriceList.add(order);
                //System.out.println("未匹配到数据，加入市价买入队列，当前队列长度："+buyMarketPriceList.size());
            }
        }
        return  match;

    }

    /**
     * 市价卖出
     * @param price
     */
    private  boolean marketSellDeal(Order order){
        boolean match=false;
        if(!CollectionUtils.isEmpty(buyLimitPriceOrderList)){//限价卖出委托不为空
            Iterator<Order> iterator = buyLimitPriceOrderList.iterator();
            while(iterator.hasNext()){//可以不用循环，防止多手场景
                Order iterOrder =iterator.next();
                iterator.remove();
                finishOrder(order,iterOrder);
                match=true;
                break;
            }
            if(!match){//没有匹配到，加入市价买入队列
                sellMarketPriceList.add(order);
                //System.out.println("未匹配到数据，加入市价卖出队列，当前队列长度："+sellMarketPriceList.size());
            }
        }
        return match;
    }

    /**
     * 限价买入
     * @param price
     */
    private  void limitBuyDeal(Order order){
        boolean match=false;
        if(!CollectionUtils.isEmpty(sellLimitPriceOrderList)){//限价卖出委托不为空
            Iterator<Order> iterator = sellLimitPriceOrderList.iterator();

            while(iterator.hasNext()){
                Order iterOrder =iterator.next();
                if(order.getPrice()>=iterOrder.getPrice()){//买入价大于等于卖出价，以买入价成交
                    iterator.remove();
                    finishOrder(order,iterOrder);
                    match=true;
                    break;
                }
            }

        }
        if(!match){//没有匹配到，加入市价买入队列
            //查询市价卖出队列
            match = limitBuyMatchMarketSell(order);
            if(!match){
                buyLimitPriceOrderList.add(order);
                //buyLimitPriceOrderList.sort(comparatorBuy);
//                int index=0;
//                for(int j=0;j<buyLimitPriceOrderList.size();j++){
//                    if(order.getPrice()>buyLimitPriceOrderList.get(j).getPrice()){//买入价格越高，越靠前
//                        index = j;
//                        break;
//                    }
//                }
//                buyLimitPriceOrderList.add(index,order);
                //System.out.println("未匹配到数据，加入限价买入队列，当前队列长度："+buyLimitPriceOrderList.size());
            }


        }
    }
    /**
     *  限价卖出
     * @param price
     */
    private  void limitSellDeal(Order order){
        boolean match=false;
        if(!CollectionUtils.isEmpty(buyLimitPriceOrderList)){//限价卖出委托不为空
//            int index = Collections.binarySearch(buyLimitPriceOrderList,order,comparatorBuy);
//            if(index>=0){
//                Order searchOrder = buyLimitPriceOrderList.remove(index);
//                finishOrder(order,searchOrder);
//                match=true;
//            }

            Iterator<Order> iterator = buyLimitPriceOrderList.iterator();

            while(iterator.hasNext()){
                Order iterOrder =iterator.next();
                if(order.getPrice()<=iterOrder.getPrice()){//买入价大于等于卖出价，以买入价成交
                    iterator.remove();
                    finishOrder(order,iterOrder);
                    match=true;
                    break;
                }
            }

        }
        if(!match){//没有匹配到，加入市价卖出入队列
            //查询市价卖出队列
            match = limitSellMatchMarketBuy(order);
            if(!match) {
                sellLimitPriceOrderList.add(order);
               // sellLimitPriceOrderList.sort(comparatorSell);
//                int index=0;
//                for(int j=0;j<sellLimitPriceOrderList.size();j++){
//                    if(order.getPrice()<sellLimitPriceOrderList.get(j).getPrice()){//卖出价格越低越靠前
//                        index = j;
//                        break;
//                    }
//                }
//                sellLimitPriceOrderList.add(index,order);
                //System.out.println("未匹配到数据，加入限价卖出队列，当前队列长度："+sellLimitPriceOrderList.size());
            }
        }
    }

    /**
     * 限价买入去市价卖出匹配，发生在限价卖出队列没有匹配到数据之后
     * @param order
     * @return
     */
    private  boolean limitBuyMatchMarketSell(Order order){
        boolean match=false;
        if(!CollectionUtils.isEmpty(sellMarketPriceList)){//限价卖出委托不为空
            Iterator<Order> iterator = sellMarketPriceList.iterator();
            while(iterator.hasNext()){
                Order iterOrder =iterator.next();
                iterator.remove();
                finishOrder(order,iterOrder);
                match=true;
                break;
            }
        }
        return match;
    }
    /**
     * 限价卖出去市价买入匹配，发生在限价买入队列没有匹配到数据之后
     * @param order
     * @return
     */
    private  boolean limitSellMatchMarketBuy(Order order){
        boolean match=false;
        if(!CollectionUtils.isEmpty(buyMarketPriceList)){//限价卖出委托不为空
            Iterator<Order> iterator = buyMarketPriceList.iterator();
            while(iterator.hasNext()){
                Order iterOrder =iterator.next();
                iterator.remove();
                finishOrder(order,iterOrder);
                match=true;
                break;
            }
        }
        return match;
    }


    private void finishOrder(Order order,Order pairOrder){
        order.setFinishTime(System.currentTimeMillis());
        pairOrder.setFinishTime(System.currentTimeMillis());
//        System.out.println("委托单["+ order.getTradeId()+"]-["+order.getTradeType()+"-"+order.getTradeDirection()+"]处理完成，耗时:"+(order.getFinishTime()-order.getInsertTime())+"ms," +
//                "对手单号为["+ pairOrder.getTradeId()+"]-["+pairOrder.getTradeType()+"-"+pairOrder.getTradeDirection()+"],耗时:"+(pairOrder.getFinishTime()-pairOrder.getInsertTime())+"ms,"+
//                "剩余市价买单："+buyMarketPriceList.size()+",市价卖单："+sellMarketPriceList.size()+
//                ",限价买单："+buyLimitPriceOrderList.size()+",限价卖单："+sellLimitPriceOrderList.size());
        finishOrder.add(order);
        finishOrder.add(pairOrder);
    }
}
class Order{
    private TradeType tradeType;
    private TradeDirection tradeDirection;
    private long tradeId;
    private int price;
    private long insertTime;
    private long finishTime;
    private long costTime;

    public Order(TradeType tradeType, TradeDirection tradeDirection, long tradeId, int price, long insertTime) {
        this.tradeType = tradeType;
        this.tradeDirection = tradeDirection;
        this.tradeId = tradeId;
        this.price = price;
        this.insertTime = insertTime;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public TradeDirection getTradeDirection() {
        return tradeDirection;
    }

    public void setTradeDirection(TradeDirection tradeDirection) {
        this.tradeDirection = tradeDirection;
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }
}
enum TradeType{
    MARKET(1),LIMIT(2);
    private int id;

    TradeType(int id) {
        this.id = id;
    }
}
enum TradeDirection{
    BUY(1),SELL(2);
    private int id;

    TradeDirection(int id) {
        this.id = id;
    }
}
