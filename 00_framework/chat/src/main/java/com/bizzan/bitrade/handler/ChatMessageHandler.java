package com.bizzan.bitrade.handler;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.entity.ChatMessageRecord;
import com.bizzan.bitrade.entity.HistoryChatMessage;
import com.bizzan.bitrade.entity.HistoryMessagePage;
import com.bizzan.bitrade.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class ChatMessageHandler implements MessageHandler {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void handleMessage(ChatMessageRecord message) {
        mongoTemplate.insert(message, "chat_message"/*+message.getOrderId()*/);

    }

    /**
     * 获取历史聊天消息
     *
     * @param message
     * @return
     */
    @Override
    public HistoryMessagePage getHistoryMessage(HistoryChatMessage message) {
        Criteria criteria = new Criteria();
        if(!StringUtils.isEmpty(message.getOrderId())) {
            criteria = Criteria.where("orderId").is(message.getOrderId());
        }
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, message.getSortFiled()));
        Query query = new Query(criteria).with(sort);
        long total = mongoTemplate.count(query, ChatMessageRecord.class, "chat_message");
        query.limit(message.getLimit()).skip((message.getPage() - 1) * message.getLimit());
        List<ChatMessageRecord> list = mongoTemplate.find(query, ChatMessageRecord.class, "chat_message");
        for (ChatMessageRecord record : list) {
            record.setSendTimeStr(DateUtils.getDateStr(record.getSendTime()));
        }
        long consult = total / message.getLimit();
        long residue = total % message.getLimit();
        long totalPage = residue == 0 ? consult : (consult + 1);
        return HistoryMessagePage.getInstance(message.getPage(), totalPage, list.size(), total, list);

    }

}