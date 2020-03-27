package com.bizzan.bitrade.pagination;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.*;
/**
 * 定义一个查询条件容器
 * @param <T>
 */
public class Criteria<T> implements Specification<T> {
    private List<Criterion> criteria = new ArrayList<Criterion>();

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (!criteria.isEmpty()) {
            List<Predicate> predicates = new ArrayList<Predicate>();
            for(Criterion c : criteria){
                predicates.add(c.toPredicate(root, query,builder));
            }
            // 将所有条件用 and 联合起来
            if (predicates.size() > 0) {
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }
        return builder.conjunction();
    }
    /**
     * 增加简单条件表达式
     */
    public void add(Criterion criterion){
        if(criterion !=null){
            criteria.add(criterion);
        }
    }

    /**
     * 排序
     * QueryBuilderUtils.sort("name","id.desc");表示先以name升序，之后以xh降序
     */
    public  Sort sort(String... fields) {
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        for(String f:fields) {
            orders.add(generateOrderStatic(f));
        }
        return new Sort(orders);
    }

    public static Sort sortStatic(String... fields) {
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        for(String f:fields) {
            orders.add(generateOrderStatic(f));
        }
        return new Sort(orders);
    }

    private static Sort.Order generateOrderStatic(String f) {
        Sort.Order order = null;
        String[] ff = f.split("\\.");
        if(ff.length>1) {
            if("desc".equals(ff[1])) {
                order = new Sort.Order(Sort.Direction.DESC,ff[0]);
            } else {
                order = new Sort.Order(Sort.Direction.ASC,ff[0]);
            }
            return order;
        }
        order = new Sort.Order(f);
        return order;
    }


}