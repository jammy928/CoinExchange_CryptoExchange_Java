package com.bizzan.bitrade.pagenation;

import lombok.Data;

import java.io.Serializable;
import java.util.List;



@Data
public class EntityPage<T extends Serializable> implements Serializable{


    private int currentPage = 1;

    private int pageSize =20;

    private long totalPage =0;

    private long count =0 ;

    private List<T> list ;
}
