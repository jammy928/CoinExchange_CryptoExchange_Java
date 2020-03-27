package com.bizzan.bitrade.dto;

import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class Pagenation<T> {

    private PageParam pageParam ;

    private long totalCount ;

    private long totalPage ;

    private List<T> list ;

    public Pagenation(int pageNo,int pageSize){
        this.pageParam.setPageNo(pageNo) ;
        this.pageParam.setPageSize(pageSize);
    }

    public Pagenation(PageParam pageParam){
        if(pageParam.getOrders().size()<1){
            pageParam.getOrders().add("id");
        }
        this.pageParam = pageParam ;
    }

    public Pagenation setData(List list , long totalCount , long totalPage){
        this.list = list ;
        this.totalCount = totalCount ;
        this.totalPage = totalPage ;
        return this ;
    }

}
