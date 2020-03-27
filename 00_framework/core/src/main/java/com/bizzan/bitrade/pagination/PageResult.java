package com.bizzan.bitrade.pagination;

import java.io.Serializable;
import java.util.List;

/**
 * @author GS
 * @description
 * @date 2018/2/28 15:48
 */

public class PageResult<T> implements Serializable {
    private List<T> content;
    private int number;
    private int size;
    private Long totalElements;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
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

    public PageResult(List<T> list, Long totalNumber) {
        this.content = list;
        this.totalElements = totalNumber;
    }

    public PageResult(List<T> content, int number, int size, Long totalElements) {
        this.content = content;
        this.number = number - 1;
        this.size = size;
        this.totalElements = totalElements;
    }
}
