package com.bizzan.bitrade.ability;

import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.ArrayList;

/**
 * @author GS
 * @Title: ${file_name}
 * @Description:
 * @date 2018/4/2417:35
 */
public interface ScreenAbility {
    ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();

    /**
     * 筛选能力
     *
     * @return 筛选断言List
     */
    ArrayList<BooleanExpression> getBooleanExpressions();

    /**
     * 获取条件
     *
     * @return
     */
    default Predicate getPredicate() {
        ArrayList<BooleanExpression> booleanExpressions = getBooleanExpressions();
        return PredicateUtils.getPredicate(booleanExpressions);
    }

    /**
     * 获取条件(添加外部筛选)
     *
     * @param booleanExpressions 条件断言
     * @return
     */
    default Predicate getPredicate(BooleanExpression... booleanExpressions) {
        ArrayList<BooleanExpression> booleanExpressionsList = getBooleanExpressions();
        for (BooleanExpression b : booleanExpressions) {
            booleanExpressionsList.add(b);
        }
        return PredicateUtils.getPredicate(booleanExpressionsList);
    }
}
