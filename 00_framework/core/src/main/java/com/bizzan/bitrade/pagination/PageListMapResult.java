package com.bizzan.bitrade.pagination;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author GS
 * @description
 * @date 2018/2/28 15:48
 */
public class PageListMapResult implements Serializable {
    private List<Map<String, Object>> content;
    private int number;
    private int size;
    private Long totalElements;

    public PageListMapResult(List<Map<String, Object>> content, int number, int size, Long totalElements) {
        this.content = content;
        this.number = number - 1;
        this.size = size;
        this.totalElements = totalElements;
    }


    public List<Map<String, Object>> getContent() {
        return content;
    }

    public void setContent(List<Map<String, Object>> content) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number - 1;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }
}
