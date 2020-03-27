package com.bizzan.bitrade.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.CtcAcceptor;
import com.bizzan.bitrade.entity.Member;

@Repository
public interface CtcAcceptorDao  extends  BaseDao<CtcAcceptor>  {
	List<CtcAcceptor> findAllByStatus(int status);
	List<CtcAcceptor> findAllByMember(Member member);
}
