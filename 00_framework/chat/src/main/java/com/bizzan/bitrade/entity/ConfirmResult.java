package com.bizzan.bitrade.entity;

import lombok.Data;

@Data
public class ConfirmResult {

    private String content ;

    private int status ; //0表示成功

    private String uidFrom ;

    private String nameFrom ;

    private String orderId ;

    public ConfirmResult(String content,int status){
        this.status = status ;
        this.content = content ;
    }
    public ConfirmResult(String content){
        this.status = 0 ;
        this.content = content ;
    }

    public  static  ConfirmResult success(String content){
        return  new ConfirmResult(content);
    }

    @Override
    public String toString() {
        return "ConfirmResult{" +
                "content='" + content + '\'' +
                ", status=" + status +
                '}';
    }
}
