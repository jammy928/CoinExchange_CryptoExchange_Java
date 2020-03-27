package com.bizzan.bitrade.pagination;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GS
 * @description
 * @date 2018/3/7 13:10
 */
public class QueryDslContext {
    private List<Expression> expressions;
    private List<EntityPath> entityPaths;
    private List<Predicate> predicates;
    private List<OrderSpecifier> orderSpecifiers;

    public QueryDslContext() {
        this.expressions = new ArrayList<>();
        this.entityPaths = new ArrayList<>();
        this.predicates = new ArrayList<>();
        this.orderSpecifiers = new ArrayList<>();
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void add(Expression expression) {
        expressions.add(expression);
    }

    public void add(EntityPath entityPath) {
        entityPaths.add(entityPath);
    }

    public void add(Predicate predicate) {
        predicates.add(predicate);
    }

    public void add(OrderSpecifier orderSpecifier) {
        orderSpecifiers.add(orderSpecifier);
    }

    public Expression[] expressionToArray() {
        return expressions.toArray(new Expression[expressions.size()]);
    }

    public EntityPath[] entityPathToArray() {
        return entityPaths.toArray(new EntityPath[entityPaths.size()]);
    }

    public Predicate[] predicatesToArray() {
        return predicates.toArray(new Predicate[predicates.size()]);
    }

    public OrderSpecifier[] orderSpecifiersToArray() {
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }


}
