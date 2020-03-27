package com.bizzan.bitrade.pagination;

import javax.persistence.criteria.*;

public interface Criterion {
    public enum Operator {
        EQ, NE, LIKE, GT, LT, GTE, LTE, AND, OR,ISNOTNULL
    }
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,CriteriaBuilder builder);
}