package com.bizzan.bitrade.pagination;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 逻辑条件表达式 用于复杂条件时使用，如但属性多对应值的OR查询等
 * @author lee
 *
 */
public class LogicalExpression implements Criterion {
    private Criterion[] criterion;  // 逻辑表达式中包含的表达式
    private Operator operator;      //计算符

    public LogicalExpression(Criterion[] pageCriteria, Operator operator) {
        this.criterion = pageCriteria;
        this.operator = operator;
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                 CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        for(int i = 0; i<this.criterion.length; i++){
            predicates.add(this.criterion[i].toPredicate(root, query, builder));
        }
        switch (operator) {
            case OR:
                return builder.or(predicates.toArray(new Predicate[predicates.size()]));
            default:
                return null;
        }
    }

}