package com.bizzan.bitrade.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MemberTransaction;

import java.util.List;
import java.util.Map;
import java.util.Date;

public interface MemberTransactionDao extends BaseDao<MemberTransaction> {

    @Query("select m from MemberTransaction as m where m.createTime between :startTime and  :endTime and m.type = :type")
    List<MemberTransaction> findAllDailyMatch(String startTime,String endTime,TransactionType type);

    /*@Query(value="select sum(amount),sum(fee) ,symbol as unit from member_transaction where type = 3 and date_format(create_time,'%Y-%m-%d')= :date group by symbol",nativeQuery = true)
    List<Object[]> getExchangeTurnover(@Param("date") String date);*/

    @Query("select sum(t.amount)  as amount from MemberTransaction t where t.flag = 0 and t.amount > 0 and t.memberId = :memberId and t.symbol = :symbol and t.type in :types")
    Map<String,Object> findMatchTransactionSum(@Param("memberId") Long memberId,@Param("symbol") String symbol,@Param("types") List<TransactionType> types);

    @Query("select sum(t.amount)  as amount from MemberTransaction t where t.flag = 0  and t.memberId = :memberId and t.symbol = :symbol and t.type = :type and t.createTime >= :startTime and t.createTime <= :endTime")
    Map<String,Object> findMatchTransactionSum(@Param("memberId") Long memberId,@Param("symbol") String symbol,@Param("type") TransactionType type,@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    @Query("select sum(t.amount)  as amount from MemberTransaction t where t.flag = 0  and t.symbol = :symbol and t.type = :type and t.createTime >= :startTime and t.createTime <= :endTime")
    Map<String,Object> findMatchTransactionSum(@Param("symbol") String symbol,@Param("type") TransactionType type,@Param("startTime") Date startTime,@Param("endTime") Date endTime);
   
    @Transactional
    @Modifying
    @Query("delete from MemberTransaction as m where m.createTime < :beforeTime and m.type = 3 and m.memberId = 1")
    int deleteHistory(@Param("beforeTime") Date beforeTime);
}
