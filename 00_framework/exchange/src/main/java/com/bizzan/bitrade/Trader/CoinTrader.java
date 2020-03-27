package com.bizzan.bitrade.Trader;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.entity.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CoinTrader {
    private String symbol;
    private KafkaTemplate<String,String> kafkaTemplate;
    //交易币种的精度
    private int coinScale = 4;
    //基币的精度
    private int baseCoinScale = 4;
    private Logger logger = LoggerFactory.getLogger(CoinTrader.class);
    //买入限价订单链表，价格从高到低排列
    private TreeMap<BigDecimal,MergeOrder> buyLimitPriceQueue;
    //卖出限价订单链表，价格从低到高排列
    private TreeMap<BigDecimal,MergeOrder> sellLimitPriceQueue;
    //买入市价订单链表，按时间从小到大排序
    private LinkedList<ExchangeOrder> buyMarketQueue;
    //卖出市价订单链表，按时间从小到大排序
    private LinkedList<ExchangeOrder> sellMarketQueue;
    //卖盘盘口信息
    private TradePlate sellTradePlate;
    //买盘盘口信息
    private TradePlate buyTradePlate;
    //是否暂停交易
    private boolean tradingHalt = false;
    private boolean ready = false;
    //交易对信息
    private ExchangeCoinPublishType publishType;
    private String clearTime;
    
    private SimpleDateFormat dateTimeFormat;
    

    public CoinTrader(String symbol){
        this.symbol = symbol;
        initialize();
    }

    /**
     * 初始化交易线程
     */
    public void initialize(){
        logger.info("init CoinTrader for symbol {}",symbol);
        //买单队列价格降序排列
        buyLimitPriceQueue = new TreeMap<>(Comparator.reverseOrder());
        //卖单队列价格升序排列
        this.sellLimitPriceQueue = new TreeMap<>(Comparator.naturalOrder());
        this.buyMarketQueue = new LinkedList<>();
        this.sellMarketQueue = new LinkedList<>();
        this.sellTradePlate = new TradePlate(symbol,ExchangeOrderDirection.SELL);
        this.buyTradePlate = new TradePlate(symbol,ExchangeOrderDirection.BUY);
        this.dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 增加限价订单到队列，买入单按从价格高到低排，卖出单按价格从低到高排
     * @param exchangeOrder
     */
    public void addLimitPriceOrder(ExchangeOrder exchangeOrder){
        if(exchangeOrder.getType() != ExchangeOrderType.LIMIT_PRICE){
            return ;
        }
        //logger.info("addLimitPriceOrder,orderId = {}", exchangeOrder.getOrderId());
        TreeMap<BigDecimal,MergeOrder> list;
        if(exchangeOrder.getDirection() == ExchangeOrderDirection.BUY){
            list = buyLimitPriceQueue;
            buyTradePlate.add(exchangeOrder);
            if(ready) {
                sendTradePlateMessage(buyTradePlate);
            }
        } else {
            list = sellLimitPriceQueue;
            sellTradePlate.add(exchangeOrder);
            if(ready) {
                sendTradePlateMessage(sellTradePlate);
            }
        }
        synchronized (list) {
            MergeOrder mergeOrder = list.get(exchangeOrder.getPrice());
            if(mergeOrder == null){
                mergeOrder = new MergeOrder();
                mergeOrder.add(exchangeOrder);
                list.put(exchangeOrder.getPrice(),mergeOrder);
            }
            else {
                mergeOrder.add(exchangeOrder);
            }
        }
    }

    public void addMarketPriceOrder(ExchangeOrder exchangeOrder){
        if(exchangeOrder.getType() != ExchangeOrderType.MARKET_PRICE){
            return ;
        }
        logger.info("addMarketPriceOrder,orderId = {}", exchangeOrder.getOrderId());
        LinkedList<ExchangeOrder> list = exchangeOrder.getDirection() == ExchangeOrderDirection.BUY ? buyMarketQueue : sellMarketQueue;
        synchronized (list) {
            list.addLast(exchangeOrder);
        }
    }

    public void trade(List<ExchangeOrder> orders) throws ParseException{
        if(tradingHalt) {
            return ;
        }
        for(ExchangeOrder order:orders){
            trade(order);
        }
    }


    /**
     * 主动交易输入的订单，交易不完成的会输入到队列
     * @param exchangeOrder
     * @throws ParseException 
     */
    public void trade(ExchangeOrder exchangeOrder) throws ParseException{
        if(tradingHalt) {
            return ;
        }
        //logger.info("trade order={}",exchangeOrder);
        if(!symbol.equalsIgnoreCase(exchangeOrder.getSymbol())){
            logger.info("unsupported symbol,coin={},base={}", exchangeOrder.getCoinSymbol(), exchangeOrder.getBaseSymbol());
            return ;
        }
        if(exchangeOrder.getAmount().compareTo(BigDecimal.ZERO) <=0 || exchangeOrder.getAmount().subtract(exchangeOrder.getTradedAmount()).compareTo(BigDecimal.ZERO)<=0){
            return ;
        }

        TreeMap<BigDecimal,MergeOrder> limitPriceOrderList;
        LinkedList<ExchangeOrder> marketPriceOrderList;
        if(exchangeOrder.getDirection() == ExchangeOrderDirection.BUY){
            limitPriceOrderList = sellLimitPriceQueue;
            marketPriceOrderList = sellMarketQueue;
        }
        else{
            limitPriceOrderList = buyLimitPriceQueue;
            marketPriceOrderList = buyMarketQueue;
        }
        if(exchangeOrder.getType() == ExchangeOrderType.MARKET_PRICE){
            //logger.info(">>>>>市价单>>>交易与限价单交易");
            //与限价单交易
            matchMarketPriceWithLPList(limitPriceOrderList, exchangeOrder);
        }else if(exchangeOrder.getType() == ExchangeOrderType.LIMIT_PRICE){
            //限价单价格必须大于0
            if(exchangeOrder.getPrice().compareTo(BigDecimal.ZERO) <= 0){
                return ;
            }
            // 抢购无需特殊处理，直接成交即可，但无法市价成交，在exchange-api做过滤控制
            // 仅分摊模式需要做特殊处理(分摊模式下，先有客户下一堆买单，然后由管理员发布卖单，根据比例分配)
            if(publishType == ExchangeCoinPublishType.FENTAN
            		&& exchangeOrder.getDirection() == ExchangeOrderDirection.SELL) {
            	logger.info(">>>>>分摊卖单>>>开始处理");
            	// 仅处在结束时间与清盘时间内的卖单需要特殊处理
				if(exchangeOrder.getTime().longValue() < dateTimeFormat.parse(clearTime).getTime()) {
					logger.info(">>>>>分摊卖单>>>处在结束时间与清盘时间内");
					//将卖单分摊成交，活动卖单无需压入卖盘显示在前端
					matchLimitPriceWithLPListByFENTAN(limitPriceOrderList, exchangeOrder, false);
					return;
				}
            }
            //logger.info(">>>>>限价单>>>交易与限价单交易");
            //先与限价单交易
            matchLimitPriceWithLPList(limitPriceOrderList, exchangeOrder,false);
            if(exchangeOrder.getAmount().compareTo(exchangeOrder.getTradedAmount()) > 0) {
                //logger.info(">>>>限价单未交易完>>>>与市价单交易>>>>");
                //后与市价单交易
                matchLimitPriceWithMPList(marketPriceOrderList, exchangeOrder);
            }
        }
    }
    /**
     * 分摊抢购模式下的交易处理规则
     * @param lpList
     * @param focusedOrder
     * @param canEnterList
     */
    
    public void matchLimitPriceWithLPListByFENTAN(TreeMap<BigDecimal,MergeOrder> lpList, ExchangeOrder focusedOrder,boolean canEnterList) {
    	List<ExchangeTrade> exchangeTrades = new ArrayList<>();
        List<ExchangeOrder> completedOrders = new ArrayList<>();
        synchronized (lpList) {
            Iterator<Map.Entry<BigDecimal,MergeOrder>> mergeOrderIterator = lpList.entrySet().iterator();
            boolean exitLoop = false;
            while (!exitLoop && mergeOrderIterator.hasNext()) {
                Map.Entry<BigDecimal,MergeOrder> entry = mergeOrderIterator.next();
                MergeOrder mergeOrder = entry.getValue();
                Iterator<ExchangeOrder> orderIterator = mergeOrder.iterator();
                //买入单需要匹配的价格不大于委托价，否则退出
                if (focusedOrder.getDirection() == ExchangeOrderDirection.BUY && mergeOrder.getPrice().compareTo(focusedOrder.getPrice()) > 0) {
                    break;
                }
                //卖出单需要匹配的价格不小于委托价，否则退出
                if (focusedOrder.getDirection() == ExchangeOrderDirection.SELL && mergeOrder.getPrice().compareTo(focusedOrder.getPrice()) < 0) {
                    break;
                }
                BigDecimal totalAmount = mergeOrder.getTotalAmount();
                while (orderIterator.hasNext()) {
                    ExchangeOrder matchOrder = orderIterator.next();
                    //处理匹配
                    ExchangeTrade trade = processMatchByFENTAN(focusedOrder, matchOrder, totalAmount);
                    exchangeTrades.add(trade);
                    //判断匹配单是否完成
                    if (matchOrder.isCompleted()) {
                        //当前匹配的订单完成交易，删除该订单
                        orderIterator.remove();
                        completedOrders.add(matchOrder);
                    }
                    //判断交易单是否完成
                    if (focusedOrder.isCompleted()) {
                        //交易完成
                        completedOrders.add(focusedOrder);
                        //退出循环
                        exitLoop = true;
                        break;
                    }
                }
                if(mergeOrder.size() == 0){
                    mergeOrderIterator.remove();
                }
            }
        }
        //如果还没有交易完，订单压入列表中
        if (focusedOrder.getTradedAmount().compareTo(focusedOrder.getAmount()) < 0 && canEnterList) {
            addLimitPriceOrder(focusedOrder);
        }
        //每个订单的匹配批量推送
        handleExchangeTrade(exchangeTrades);
        if(completedOrders.size() > 0){
            orderCompleted(completedOrders);
            TradePlate plate = focusedOrder.getDirection() == ExchangeOrderDirection.BUY ? sellTradePlate : buyTradePlate;
            sendTradePlateMessage(plate);
        }
    }
    /**
     * 限价委托单与限价队列匹配
     * @param lpList 限价对手单队列
     * @param focusedOrder 交易订单
     */
    public void matchLimitPriceWithLPList(TreeMap<BigDecimal,MergeOrder> lpList, ExchangeOrder focusedOrder,boolean canEnterList){
        List<ExchangeTrade> exchangeTrades = new ArrayList<>();
        List<ExchangeOrder> completedOrders = new ArrayList<>();
        synchronized (lpList) {
            Iterator<Map.Entry<BigDecimal,MergeOrder>> mergeOrderIterator = lpList.entrySet().iterator();
            boolean exitLoop = false;
            while (!exitLoop && mergeOrderIterator.hasNext()) {
                Map.Entry<BigDecimal,MergeOrder> entry = mergeOrderIterator.next();
                MergeOrder mergeOrder = entry.getValue();
                Iterator<ExchangeOrder> orderIterator = mergeOrder.iterator();
                //买入单需要匹配的价格不大于委托价，否则退出
                if (focusedOrder.getDirection() == ExchangeOrderDirection.BUY && mergeOrder.getPrice().compareTo(focusedOrder.getPrice()) > 0) {
                    break;
                }
                //卖出单需要匹配的价格不小于委托价，否则退出
                if (focusedOrder.getDirection() == ExchangeOrderDirection.SELL && mergeOrder.getPrice().compareTo(focusedOrder.getPrice()) < 0) {
                    break;
                }
                while (orderIterator.hasNext()) {
                    ExchangeOrder matchOrder = orderIterator.next();
                    //处理匹配
                    ExchangeTrade trade = processMatch(focusedOrder, matchOrder);
                    exchangeTrades.add(trade);
                    //判断匹配单是否完成
                    if (matchOrder.isCompleted()) {
                        //当前匹配的订单完成交易，删除该订单
                        orderIterator.remove();
                        completedOrders.add(matchOrder);
                    }
                    //判断交易单是否完成
                    if (focusedOrder.isCompleted()) {
                        //交易完成
                        completedOrders.add(focusedOrder);
                        //退出循环
                        exitLoop = true;
                        break;
                    }
                }
                if(mergeOrder.size() == 0){
                    mergeOrderIterator.remove();
                }
            }
        }
        //如果还没有交易完，订单压入列表中
        if (focusedOrder.getTradedAmount().compareTo(focusedOrder.getAmount()) < 0 && canEnterList) {
            addLimitPriceOrder(focusedOrder);
        }
        //每个订单的匹配批量推送
        handleExchangeTrade(exchangeTrades);
        if(completedOrders.size() > 0){
            orderCompleted(completedOrders);
            TradePlate plate = focusedOrder.getDirection() == ExchangeOrderDirection.BUY ? sellTradePlate : buyTradePlate;
            sendTradePlateMessage(plate);
        }
    }

    /**
     * 限价委托单与市价队列匹配
     * @param mpList 市价对手单队列
     * @param focusedOrder 交易订单
     */
    public void matchLimitPriceWithMPList(LinkedList<ExchangeOrder> mpList,ExchangeOrder focusedOrder){
        List<ExchangeTrade> exchangeTrades = new ArrayList<>();
        List<ExchangeOrder> completedOrders = new ArrayList<>();
        synchronized (mpList) {
            Iterator<ExchangeOrder> iterator = mpList.iterator();
            while (iterator.hasNext()) {
                ExchangeOrder matchOrder = iterator.next();
                ExchangeTrade trade = processMatch(focusedOrder, matchOrder);
                logger.info(">>>>>"+trade);
                if(trade != null){
                    exchangeTrades.add(trade);
                }
                //判断匹配单是否完成，市价单amount为成交量
                if(matchOrder.isCompleted()){
                    iterator.remove();
                    completedOrders.add(matchOrder);
                }
                //判断吃单是否完成，判断成交量是否完成
                if (focusedOrder.isCompleted()) {
                    //交易完成
                    completedOrders.add(focusedOrder);
                    //退出循环
                    break;
                }
            }
        }
        //如果还没有交易完，订单压入列表中
        if (focusedOrder.getTradedAmount().compareTo(focusedOrder.getAmount()) < 0) {
            addLimitPriceOrder(focusedOrder);
        }
        //每个订单的匹配批量推送
        handleExchangeTrade(exchangeTrades);
        orderCompleted(completedOrders);
    }


    /**
     * 市价委托单与限价对手单列表交易
     * @param lpList  限价对手单列表
     * @param focusedOrder 待交易订单
     */
    public void matchMarketPriceWithLPList(TreeMap<BigDecimal,MergeOrder> lpList, ExchangeOrder focusedOrder){
        List<ExchangeTrade> exchangeTrades = new ArrayList<>();
        List<ExchangeOrder> completedOrders = new ArrayList<>();
        synchronized (lpList) {
            Iterator<Map.Entry<BigDecimal,MergeOrder>> mergeOrderIterator = lpList.entrySet().iterator();
            boolean exitLoop = false;
            while (!exitLoop && mergeOrderIterator.hasNext()) {
                Map.Entry<BigDecimal,MergeOrder> entry = mergeOrderIterator.next();
                MergeOrder mergeOrder = entry.getValue();
                Iterator<ExchangeOrder> orderIterator = mergeOrder.iterator();
                while (orderIterator.hasNext()) {
                    ExchangeOrder matchOrder = orderIterator.next();
                    //处理匹配
                    ExchangeTrade trade = processMatch(focusedOrder, matchOrder);
                    if (trade != null) {
                        exchangeTrades.add(trade);
                    }
                    //判断匹配单是否完成
                    if (matchOrder.isCompleted()) {
                        //当前匹配的订单完成交易，删除该订单
                        orderIterator.remove();
                        completedOrders.add(matchOrder);
                    }
                    //判断焦点订单是否完成
                    if (focusedOrder.isCompleted()) {
                        completedOrders.add(focusedOrder);
                        //退出循环
                        exitLoop = true;
                        break;
                    }
                }
                if(mergeOrder.size() == 0){
                    mergeOrderIterator.remove();
                }
            }
        }
        //如果还没有交易完，订单压入列表中,市价买单按成交量算
        if (focusedOrder.getDirection() == ExchangeOrderDirection.SELL&&focusedOrder.getTradedAmount().compareTo(focusedOrder.getAmount()) < 0
                || focusedOrder.getDirection() == ExchangeOrderDirection.BUY&& focusedOrder.getTurnover().compareTo(focusedOrder.getAmount()) < 0) {
            addMarketPriceOrder(focusedOrder);
        }
        //每个订单的匹配批量推送
        handleExchangeTrade(exchangeTrades);
        if(completedOrders.size() > 0){
            orderCompleted(completedOrders);
            TradePlate plate = focusedOrder.getDirection() == ExchangeOrderDirection.BUY ? sellTradePlate : buyTradePlate;
            sendTradePlateMessage(plate);
        }
    }

    /**
     * 计算委托单剩余可成交的数量
     * @param order 委托单
     * @param dealPrice 成交价
     * @return
     */
    private BigDecimal calculateTradedAmount(ExchangeOrder order, BigDecimal dealPrice){
        if(order.getDirection() == ExchangeOrderDirection.BUY && order.getType() == ExchangeOrderType.MARKET_PRICE){
            //剩余成交量
            BigDecimal leftTurnover = order.getAmount().subtract(order.getTurnover());
            return leftTurnover.divide(dealPrice,coinScale,BigDecimal.ROUND_DOWN);
        }
        else{
            return  order.getAmount().subtract(order.getTradedAmount());
        }
    }

    /**
     * 调整市价单剩余成交额，当剩余成交额不足时设置订单完成
     * @param order
     * @param dealPrice
     * @return
     */
    private BigDecimal adjustMarketOrderTurnover(ExchangeOrder order, BigDecimal dealPrice){
        if(order.getDirection() == ExchangeOrderDirection.BUY && order.getType() == ExchangeOrderType.MARKET_PRICE){
            BigDecimal leftTurnover = order.getAmount().subtract(order.getTurnover());
            if(leftTurnover.divide(dealPrice,coinScale,BigDecimal.ROUND_DOWN)
                    .compareTo(BigDecimal.ZERO)==0){
                order.setTurnover(order.getAmount());
                return leftTurnover;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 处理两个匹配的委托订单
     * @param focusedOrder 焦点单
     * @param matchOrder 匹配单
     * @return
     */
    private ExchangeTrade processMatch(ExchangeOrder focusedOrder, ExchangeOrder matchOrder){
        //需要交易的数量，成交量,成交价，可用数量
        BigDecimal needAmount,dealPrice,availAmount;
        //如果匹配单是限价单，则以其价格为成交价
        if(matchOrder.getType() == ExchangeOrderType.LIMIT_PRICE){
            dealPrice = matchOrder.getPrice();
        }
        else {
            dealPrice = focusedOrder.getPrice();
        }
        //成交价必须大于0
        if(dealPrice.compareTo(BigDecimal.ZERO) <= 0){
            return null;
        }
        needAmount = calculateTradedAmount(focusedOrder,dealPrice);
        availAmount = calculateTradedAmount(matchOrder,dealPrice);
        //计算成交量
        BigDecimal tradedAmount = (availAmount.compareTo(needAmount) >= 0 ? needAmount : availAmount);
        logger.info("dealPrice={},amount={}",dealPrice,tradedAmount);
        //如果成交额为0说明剩余额度无法成交，退出
        if(tradedAmount.compareTo(BigDecimal.ZERO) == 0){
            return null;
        }

        //计算成交额,成交额要保留足够精度
        BigDecimal turnover = tradedAmount.multiply(dealPrice);
        matchOrder.setTradedAmount(matchOrder.getTradedAmount().add(tradedAmount));
        matchOrder.setTurnover(matchOrder.getTurnover().add(turnover));
        focusedOrder.setTradedAmount(focusedOrder.getTradedAmount().add(tradedAmount));
        focusedOrder.setTurnover(focusedOrder.getTurnover().add(turnover));

        //创建成交记录
        ExchangeTrade exchangeTrade = new ExchangeTrade();
        exchangeTrade.setSymbol(symbol);
        exchangeTrade.setAmount(tradedAmount);
        exchangeTrade.setDirection(focusedOrder.getDirection());
        exchangeTrade.setPrice(dealPrice);
        exchangeTrade.setBuyTurnover(turnover);
        exchangeTrade.setSellTurnover(turnover);
        //校正市价单剩余成交额
        if(ExchangeOrderType.MARKET_PRICE == focusedOrder.getType() && focusedOrder.getDirection() == ExchangeOrderDirection.BUY){
            BigDecimal adjustTurnover = adjustMarketOrderTurnover(focusedOrder,dealPrice);
            exchangeTrade.setBuyTurnover(turnover.add(adjustTurnover));
        }
        else if(ExchangeOrderType.MARKET_PRICE == matchOrder.getType() && matchOrder.getDirection() == ExchangeOrderDirection.BUY){
            BigDecimal adjustTurnover = adjustMarketOrderTurnover(matchOrder,dealPrice);
            exchangeTrade.setBuyTurnover(turnover.add(adjustTurnover));
        }

        if (focusedOrder.getDirection() == ExchangeOrderDirection.BUY) {
            exchangeTrade.setBuyOrderId(focusedOrder.getOrderId());
            exchangeTrade.setSellOrderId(matchOrder.getOrderId());
        } else {
            exchangeTrade.setBuyOrderId(matchOrder.getOrderId());
            exchangeTrade.setSellOrderId(focusedOrder.getOrderId());
        }

        exchangeTrade.setTime(Calendar.getInstance().getTimeInMillis());
        if(matchOrder.getType() == ExchangeOrderType.LIMIT_PRICE){
            if(matchOrder.getDirection() == ExchangeOrderDirection.BUY){
                buyTradePlate.remove(matchOrder,tradedAmount);
            }
            else{
                sellTradePlate.remove(matchOrder,tradedAmount);
            }
        }
        return  exchangeTrade;
    }

    /**
     * 处理两个匹配的委托订单
     * @param focusedOrder 焦点单
     * @param matchOrder 匹配单
     * @return
     */
    private ExchangeTrade processMatchByFENTAN(ExchangeOrder focusedOrder, ExchangeOrder matchOrder, BigDecimal totalAmount){
        //需要交易的数量，成交量,成交价，可用数量
        BigDecimal dealPrice;
        //如果匹配单是限价单，则以其价格为成交价
        if(matchOrder.getType() == ExchangeOrderType.LIMIT_PRICE){
            dealPrice = matchOrder.getPrice();
        }
        else {
            dealPrice = focusedOrder.getPrice();
        }
        //成交价必须大于0
        if(dealPrice.compareTo(BigDecimal.ZERO) <= 0){
            return null;
        }
        // 成交数 = 发行卖单总数*匹配单数量占比（例：1.2345%）
        //计算成交量
        BigDecimal tradedAmount = focusedOrder.getAmount().multiply(matchOrder.getAmount().divide(totalAmount, 8, BigDecimal.ROUND_HALF_DOWN)).setScale(8, BigDecimal.ROUND_HALF_DOWN);
        logger.info("dealPrice={},amount={}",dealPrice,tradedAmount);
        //如果成交额为0说明剩余额度无法成交，退出
        if(tradedAmount.compareTo(BigDecimal.ZERO) == 0){
            return null;
        }

        //计算成交额,成交额要保留足够精度
        BigDecimal turnover = tradedAmount.multiply(dealPrice).setScale(8, BigDecimal.ROUND_HALF_DOWN);
        matchOrder.setTradedAmount(matchOrder.getTradedAmount().add(tradedAmount).setScale(8, BigDecimal.ROUND_HALF_DOWN));
        matchOrder.setTurnover(matchOrder.getTurnover().add(turnover).setScale(8, BigDecimal.ROUND_HALF_DOWN));
        focusedOrder.setTradedAmount(focusedOrder.getTradedAmount().add(tradedAmount).setScale(8, BigDecimal.ROUND_HALF_DOWN));
        focusedOrder.setTurnover(focusedOrder.getTurnover().add(turnover).setScale(8, BigDecimal.ROUND_HALF_DOWN));

        //创建成交记录
        ExchangeTrade exchangeTrade = new ExchangeTrade();
        exchangeTrade.setSymbol(symbol);
        exchangeTrade.setAmount(tradedAmount);
        exchangeTrade.setDirection(focusedOrder.getDirection());
        exchangeTrade.setPrice(dealPrice);
        exchangeTrade.setBuyTurnover(turnover);
        exchangeTrade.setSellTurnover(turnover);

        if (focusedOrder.getDirection() == ExchangeOrderDirection.BUY) {
            exchangeTrade.setBuyOrderId(focusedOrder.getOrderId());
            exchangeTrade.setSellOrderId(matchOrder.getOrderId());
        } else {
            exchangeTrade.setBuyOrderId(matchOrder.getOrderId());
            exchangeTrade.setSellOrderId(focusedOrder.getOrderId());
        }

        exchangeTrade.setTime(Calendar.getInstance().getTimeInMillis());
        if(matchOrder.getType() == ExchangeOrderType.LIMIT_PRICE){
            if(matchOrder.getDirection() == ExchangeOrderDirection.BUY){
                buyTradePlate.remove(matchOrder,tradedAmount);
            }
            else{
                sellTradePlate.remove(matchOrder,tradedAmount);
            }
        }
        return  exchangeTrade;
    }
    
    public void handleExchangeTrade(List<ExchangeTrade> trades){
        //logger.info("handleExchangeTrade:{}", trades);
        if(trades.size() > 0) {
            int maxSize = 1000;
            //发送消息，key为交易对符号
            if(trades.size() > maxSize) {
                int size = trades.size();
                for(int index = 0;index < size;index += maxSize){
                    int length = (size - index) > maxSize ? maxSize : size - index;
                    List<ExchangeTrade> subTrades = trades.subList(index,index + length);
                    kafkaTemplate.send("exchange-trade",JSON.toJSONString(subTrades));
                }
            }
            else {
                kafkaTemplate.send("exchange-trade",JSON.toJSONString(trades));
            }
        }
    }

    /**
     * 订单完成，执行消息通知,订单数超1000个要拆分发送
     * @param orders
     */
    public  void orderCompleted(List<ExchangeOrder> orders){
        //logger.info("orderCompleted ,order={}",orders);
        if(orders.size() > 0) {
            int maxSize = 1000;
            if(orders.size() > maxSize){
                int size = orders.size();
                for(int index = 0;index < size;index += maxSize){
                    int length = (size - index) > maxSize ? maxSize : size - index;
                    List<ExchangeOrder> subOrders = orders.subList(index,index + length);
                    kafkaTemplate.send("exchange-order-completed", JSON.toJSONString(subOrders));
                }
            }
            else {
                kafkaTemplate.send("exchange-order-completed", JSON.toJSONString(orders));
            }
        }
    }

    /**
     * 发送盘口变化消息
     * @param plate
     */
    public void sendTradePlateMessage(TradePlate plate){
        //防止并发引起数组越界，造成盘口倒挂
        synchronized (plate) {
            kafkaTemplate.send("exchange-trade-plate", JSON.toJSONString(plate));
        }
    }

    /**
     * 取消委托订单
     * @param exchangeOrder
     * @return
     */
    public ExchangeOrder cancelOrder(ExchangeOrder exchangeOrder){
        logger.info("cancelOrder,orderId={}", exchangeOrder.getOrderId());
        if(exchangeOrder.getType() == ExchangeOrderType.MARKET_PRICE){
            //处理市价单
            Iterator<ExchangeOrder> orderIterator;
            List<ExchangeOrder> list = null;
            if(exchangeOrder.getDirection() == ExchangeOrderDirection.BUY){
                list = this.buyMarketQueue;
            } else{
                list = this.sellMarketQueue;
            }
            synchronized (list) {
                orderIterator = list.iterator();
                while ((orderIterator.hasNext())) {
                    ExchangeOrder order = orderIterator.next();
                    if (order.getOrderId().equalsIgnoreCase(exchangeOrder.getOrderId())) {
                        orderIterator.remove();
                        onRemoveOrder(order);
                        return order;
                    }
                }
            }
        } else {
            //处理限价单
            TreeMap<BigDecimal,MergeOrder> list = null;
            Iterator<MergeOrder> mergeOrderIterator;
            if(exchangeOrder.getDirection() == ExchangeOrderDirection.BUY){
                list = this.buyLimitPriceQueue;
            } else{
                list = this.sellLimitPriceQueue;
            }
            synchronized (list) {
                MergeOrder mergeOrder = list.get(exchangeOrder.getPrice());
                if(mergeOrder!=null) {
                    Iterator<ExchangeOrder> orderIterator = mergeOrder.iterator();
                    while (orderIterator.hasNext()) {
                        ExchangeOrder order = orderIterator.next();
                        if (order.getOrderId().equalsIgnoreCase(exchangeOrder.getOrderId())) {
                            orderIterator.remove();
                            if (mergeOrder.size() == 0) {
                                list.remove(exchangeOrder.getPrice());
                            }
                            onRemoveOrder(order);
                            return order;
                        }
                    }
                }
            }
        }
        return  null;
    }

    public void onRemoveOrder(ExchangeOrder order){
        if (order.getType() == ExchangeOrderType.LIMIT_PRICE) {
            if (order.getDirection() == ExchangeOrderDirection.BUY) {
                buyTradePlate.remove(order);
                sendTradePlateMessage(buyTradePlate);
            } else {
                sellTradePlate.remove(order);
                sendTradePlateMessage(sellTradePlate);
            }
        }
    }



    public TradePlate getTradePlate(ExchangeOrderDirection direction){
        if(direction == ExchangeOrderDirection.BUY){
            return buyTradePlate;
        }
        else{
            return sellTradePlate;
        }
    }



    /**
     * 查询交易器里的订单
     * @param orderId
     * @param type
     * @param direction
     * @return
     */
    public ExchangeOrder findOrder(String orderId,ExchangeOrderType type,ExchangeOrderDirection direction){
        if(type == ExchangeOrderType.MARKET_PRICE){
            LinkedList<ExchangeOrder> list;
            if(direction == ExchangeOrderDirection.BUY){
                list = this.buyMarketQueue;
            } else{
                list = this.sellMarketQueue;
            }
            synchronized (list) {
                Iterator<ExchangeOrder> orderIterator = list.iterator();
                while ((orderIterator.hasNext())) {
                    ExchangeOrder order = orderIterator.next();
                    if (order.getOrderId().equalsIgnoreCase(orderId)) {
                        return order;
                    }
                }
            }
        } else {
            TreeMap<BigDecimal,MergeOrder> list;
            if(direction == ExchangeOrderDirection.BUY){
                list = this.buyLimitPriceQueue;
            } else{
                list = this.sellLimitPriceQueue;
            }
            synchronized (list) {
                Iterator<Map.Entry<BigDecimal,MergeOrder>> mergeOrderIterator = list.entrySet().iterator();
                while (mergeOrderIterator.hasNext()) {
                    Map.Entry<BigDecimal,MergeOrder> entry = mergeOrderIterator.next();
                    MergeOrder mergeOrder = entry.getValue();
                    Iterator<ExchangeOrder> orderIterator = mergeOrder.iterator();
                    while ((orderIterator.hasNext())) {
                        ExchangeOrder order = orderIterator.next();
                        if (order.getOrderId().equalsIgnoreCase(orderId)) {
                            return order;
                        }
                    }
                }
            }
        }
        return null;
    }

    public TreeMap<BigDecimal,MergeOrder> getBuyLimitPriceQueue() {
        return buyLimitPriceQueue;
    }

    public LinkedList<ExchangeOrder> getBuyMarketQueue() {
        return buyMarketQueue;
    }

    public TreeMap<BigDecimal,MergeOrder> getSellLimitPriceQueue() {
        return sellLimitPriceQueue;
    }

    public LinkedList<ExchangeOrder> getSellMarketQueue() {
        return sellMarketQueue;
    }

    public void setKafkaTemplate(KafkaTemplate<String,String> template){
        this.kafkaTemplate = template;
    }
    public void setCoinScale(int scale){
        this.coinScale = scale;
    }

    public void setBaseCoinScale(int scale){
        this.baseCoinScale = scale;
    }

    public boolean isTradingHalt(){
        return this.tradingHalt;
    }

    /**
     * 暂停交易,不接收新的订单
     */
    public void haltTrading(){
        this.tradingHalt = true;
    }

    /**
     * 恢复交易
     */
    public void resumeTrading(){
        this.tradingHalt = false;
    }

    public void stopTrading(){
        //TODO:停止交易，取消当前所有订单
    }

    public boolean getReady(){
        return this.ready;
    }

    public void setReady(boolean ready){
        this.ready = ready;
    }
    public void setPublishType(ExchangeCoinPublishType publishType) {
    	this.publishType = publishType;
    }
    public void setClearTime(String clearTime) {
    	this.clearTime = clearTime;
    }
    public int getLimitPriceOrderCount(ExchangeOrderDirection direction){
        int count = 0;
        TreeMap<BigDecimal,MergeOrder> queue = direction == ExchangeOrderDirection.BUY ? buyLimitPriceQueue : sellLimitPriceQueue;
        Iterator<Map.Entry<BigDecimal,MergeOrder>> mergeOrderIterator = queue.entrySet().iterator();
        while (mergeOrderIterator.hasNext()) {
            Map.Entry<BigDecimal,MergeOrder> entry = mergeOrderIterator.next();
            MergeOrder mergeOrder = entry.getValue();
            count += mergeOrder.size();
        }
        return count;
    }
}
