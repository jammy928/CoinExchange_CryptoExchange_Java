package com.bizzan.bitrade.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.bizzan.bitrade.entity.MemberDeposit;

import java.util.List;

public interface MemberDepositDao extends JpaRepository<MemberDeposit,Long>,QueryDslPredicateExecutor<MemberDeposit>{
    MemberDeposit findByAddressAndTxid(String address,String txid);
    MemberDeposit findByTxid(String txid);
    
    @Query(value="select unit ,sum(amount) from member_deposit where date_format(create_time,'%Y-%m-%d') = :date group by unit",nativeQuery = true)
    List<Object[]> getDepositStatistics(@Param("date")String date);

    @Query("select max(a.id) as id from MemberDeposit a")
    Long getMaxId();
}
