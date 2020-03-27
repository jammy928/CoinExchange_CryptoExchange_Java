package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.OrderDetailAggregationRepository;
import com.bizzan.bitrade.dto.PageParam;
import com.bizzan.bitrade.dto.Pagenation;
import com.bizzan.bitrade.entity.OrderDetailAggregation;
import com.bizzan.bitrade.entity.OrderTypeEnum;
import com.bizzan.bitrade.service.base.MongoBaseService;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.bson.types.Decimal128;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class OrderDetailAggregationService extends MongoBaseService<OrderDetailAggregation> {

    @Autowired
    private OrderDetailAggregationRepository orderDetailAggregationRepository;

  /*  @Autowired
    private MemberService memberService ;*/

    public OrderDetailAggregation save(OrderDetailAggregation aggregation) {
        return orderDetailAggregationRepository.save(aggregation);
    }

   /* public Pagenation<OrderDetailAggregation> getDetail(PageParam pageParam, Long memberId, String coinName, OrderTypeEnum orderType) {

        Criteria criteria = new Criteria();
        criteria.where("1").equals("1");
        if (memberId != 0)
            criteria.and("memberId").is(memberId);
        if (orderType != null)
            criteria.and("type").is(orderType);
        if (!StringUtils.isEmpty(coinName))
            criteria.and("coinName").is(coinName);
        Query query = new Query(criteria);
        return page(pageParam, query, OrderDetailAggregation.class, "order_detail_aggregation");
    }
*/
    public List<Map> queryStatistics(long start, long end) {
        ProjectionOperation projectionOperation = Aggregation.project("unit", "fee", "time");
        Criteria operator = Criteria.where("unit").ne("").andOperator(
                Criteria.where("time").gte(start),
                Criteria.where("time").lt(end)
        );
        MatchOperation matchOperation = Aggregation.match(operator);

        GroupOperation groupOperation = Aggregation.group("unit").sum("fee").as("feeSum");

        /*new AggregationExpression() {
            @Override
            public DBObject toDbObject(AggregationOperationContext aggregationOperationContext) {
                DBObject basicDBObject = aggregationOperationContext.getMappedObject( new BasicDBObject("fee",aggregationOperationContext.getReference("fee")));
                return basicDBObject;
            }
        }*/

        /*AggregationExpression expression = */
        // 组合条件
        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, matchOperation, groupOperation);
        // 执行操作
        AggregationResults<Map> aggregationResults = this.mongoTemplate.aggregate(aggregation, "order_detail_aggregation", Map.class);
        List<Map> list = aggregationResults.getMappedResults();
        if (list.size() > 0) {
            list = list.stream().filter(x ->
                    x.get("_id") != null && StringUtils.isNotBlank(x.get("_id").toString())
            ).map(x -> {
                HashMap map = new HashMap(2);
                map.put("name", x.get("_id"));
                map.put("sum", new BigDecimal(x.get("feeSum").toString()).setScale(4, BigDecimal.ROUND_HALF_UP));
                return map;
            }).collect(Collectors.toList());
        }
        return list;
    }

    public List<OrderDetailAggregation> queryStatisticsByUnit(long start, long end,String unit) {
        List<OrderDetailAggregation> list=orderDetailAggregationRepository.findAllByTimeGreaterThanEqualAndTimeLessThanAndUnit(start, end, unit);
        return list;
    }


}
