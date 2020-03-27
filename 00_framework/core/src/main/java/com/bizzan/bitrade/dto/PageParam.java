package com.bizzan.bitrade.dto;

import lombok.Data;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PageParam implements Serializable {

    private int pageNo = 1;

    private int pageSize = 15;

    private List<String> orders = new ArrayList<>();

    private Sort.Direction direction = Sort.Direction.DESC;
}
