package com.bizzan.bitrade.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bizzan.bitrade.entity.OrderDetailAggregation;

import java.util.List;

public interface OrderDetailAggregationRepository extends MongoRepository<OrderDetailAggregation,String>{

    //List<OrderDetailAggregation> findAll(Specification<OrderDetailAggregation> specification);

    //Page<OrderDetailAggregation> findAll(Specification<OrderDetailAggregation> specification, Pageable pageable);

    List<OrderDetailAggregation> findAllByTimeGreaterThanEqualAndTimeLessThanAndUnit(long var1,long var2,String var3);


}

