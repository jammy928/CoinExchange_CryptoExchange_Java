package com.bizzan.bitrade.handler;

import com.bizzan.bitrade.entity.ChatMessageRecord;
import com.bizzan.bitrade.entity.HistoryChatMessage;
import com.bizzan.bitrade.entity.HistoryMessagePage;

public interface MessageHandler {

    void handleMessage(ChatMessageRecord message);

    HistoryMessagePage getHistoryMessage(HistoryChatMessage message);
}
