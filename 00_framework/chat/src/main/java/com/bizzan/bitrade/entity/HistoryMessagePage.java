package com.bizzan.bitrade.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class HistoryMessagePage {

    private int currentPage ;
    private long totalPage ;
    private int currentCount ;
    private long totalCount ;

    private List<ChatMessageRecord> data ;

    public static HistoryMessagePage getInstance
            (int currentPage, long totalPage,int currentCount,long totalCount,
            List<ChatMessageRecord> data){
        return new HistoryMessagePage(currentPage,totalPage,currentCount,totalCount,data);
    }

}
