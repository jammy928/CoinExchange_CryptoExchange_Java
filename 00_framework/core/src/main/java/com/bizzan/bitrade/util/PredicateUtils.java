package com.bizzan.bitrade.util;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.ArrayList;
import java.util.List;

public class PredicateUtils {
    /**
     * 将QueryDsl 查询断言List 返回成条件
     *
     * @param booleanExpressions
     * @return
     */
    public static Predicate getPredicate(List<BooleanExpression> booleanExpressions) {
        if (booleanExpressions.size() == 0) {
            return null;
        }
        BooleanExpression booleanExpression = null;
        for (int i = 0; i < booleanExpressions.size(); i++) {
            if (i == 0) {
                booleanExpression = booleanExpressions.get(i);
            }
            booleanExpression = booleanExpression.and(booleanExpressions.get(i));
        }
        return booleanExpression;
    }

}
