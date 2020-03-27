package com.bizzan.bitrade.entity.transform;

import lombok.Data;

import java.util.List;

/**
 *
 * @author wqx
 * @date 2018-5-14 12:30:47
 */
@Data
public class Special<E> {
    private List<E> context;
}
