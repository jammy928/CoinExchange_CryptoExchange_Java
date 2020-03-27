package com.bizzan.bitrade.service.Base;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.pagination.PageListMapResult;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.pagination.QueryDslContext;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author GS
 * @description
 * @date 2018/1/18 10:29
 */
@Component
public class BaseService<T> extends TopBaseService {

    //JPA查询工厂
    @Autowired
    protected JPAQueryFactory queryFactory;

    /**
     * 查询列表
     *
     * @param pageNo             分页参数
     * @param pageSize           分页大小
     * @param predicateList      查询条件
     * @param entityPathBase     查询表
     * @param orderSpecifierList 排序条件
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<T> queryDsl(Integer pageNo, Integer pageSize, List<Predicate> predicateList, EntityPathBase<T> entityPathBase, List<OrderSpecifier> orderSpecifierList) {
        List<T> list;
        //查询表
        JPAQuery<T> jpaQuery = queryFactory.selectFrom(entityPathBase);
        //查询条件
        if (predicateList != null && predicateList.size() > 0) {
            jpaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
        }
        //排序方式
        if (orderSpecifierList != null && orderSpecifierList.size() > 0) {
            jpaQuery.orderBy(orderSpecifierList.toArray(new OrderSpecifier[orderSpecifierList.size()]));
        }
        //分页查询
        if (pageNo != null && pageSize != null) {
            list = jpaQuery.offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
        } else {
            list = jpaQuery.fetch();
        }
        return new PageResult<>(list, pageNo, pageSize, jpaQuery.fetchCount());
    }


    /**
     * 查询单个
     *
     * @param predicate      查询条件
     * @param entityPathBase 查询表
     * @return
     */
    @Transactional(readOnly = true)
    public T queryOneDsl(Predicate predicate, EntityPathBase<T> entityPathBase) {
        return queryFactory.selectFrom(entityPathBase).where(predicate).fetchFirst();
    }

    //多表联合查询

    /**
     * @param expressions        查询列表
     * @param entityPaths        查询表
     * @param predicates         条件
     * @param orderSpecifierList 排序
     * @param pageNo             页码
     * @param pageSize           页面大小
     */
    @Transactional(readOnly = true)
    public PageListMapResult queryDslForPageListResult(
            List<Expression> expressions,
            List<EntityPath> entityPaths,
            List<Predicate> predicates,
            List<OrderSpecifier> orderSpecifierList,
            Integer pageNo,
            Integer pageSize) {
        JPAQuery<Tuple> jpaQuery = queryFactory.select(expressions.toArray(new Expression[expressions.size()]))
                .from(entityPaths.toArray(new EntityPath[entityPaths.size()]))
                .where(predicates.toArray(new Predicate[predicates.size()]));
        List<Tuple> tuples = jpaQuery.orderBy(orderSpecifierList.toArray(new OrderSpecifier[orderSpecifierList.size()]))
                .offset((pageNo - 1) * pageSize).limit(pageSize)
                .fetch();
        List<Map<String, Object>> list = new LinkedList<>();//返回结果
        //封装结果
        for (int i = 0; i < tuples.size(); i++) {
            //遍历tuples
            Map<String, Object> map = new LinkedHashMap<>();//一条信息
            for (Expression expression : expressions) {
                map.put(expression.toString().split(" as ")[1],//别名作为Key
                        tuples.get(i).get(expression));//获取结果
            }
            list.add(map);
        }
        PageListMapResult pageListMapResult = new PageListMapResult(list, pageNo, pageSize, jpaQuery.fetchCount());//分页封装
        return pageListMapResult;
    }

    @Transactional(readOnly = true)
    public PageListMapResult queryDslForPageListResult(QueryDslContext qdc, Integer pageNo, Integer pageSize) {
        JPAQuery<Tuple> jpaQuery = queryFactory.select(qdc.expressionToArray())
                .from(qdc.entityPathToArray())
                .where(qdc.predicatesToArray());
        List<Tuple> tuples = jpaQuery.orderBy(qdc.orderSpecifiersToArray())
                .offset((pageNo - 1) * pageSize).limit(pageSize)
                .fetch();
        List<Map<String, Object>> list = new LinkedList<>();//返回结果
        //封装结果
        for (int i = 0; i < tuples.size(); i++) {
            //遍历tuples
            Map<String, Object> map = new LinkedHashMap<>();//一条信息
            for (Expression expression : qdc.getExpressions()) {
                map.put(expression.toString().split(" as ")[1],//别名作为Key
                        tuples.get(i).get(expression));//获取结果
            }
            list.add(map);
        }
        PageListMapResult pageListMapResult = new PageListMapResult(list, pageNo, pageSize, jpaQuery.fetchCount());//分页封装
        return pageListMapResult;
    }
}
