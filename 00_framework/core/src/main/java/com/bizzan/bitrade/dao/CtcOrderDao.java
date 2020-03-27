package com.bizzan.bitrade.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.CtcOrder;
import com.bizzan.bitrade.entity.Member;

@Repository
public interface CtcOrderDao  extends  BaseDao<CtcOrder>{
	Coin findByUnit(String unit);
	
	List<CtcOrder> findAllByMember(Member member);
    
    List<CtcOrder> findAllByAcceptor(Member acceptor);
    
    List<CtcOrder> findAllByStatus(int status);
    
    List<CtcOrder> findAllByMemberAndStatus(Member member, int status);
    
    List<CtcOrder> findAllByAcceptorAndStatus(Member acceptor, int status);

	List<CtcOrder> findAllByIdAndMember(Long id, Member member);
}
