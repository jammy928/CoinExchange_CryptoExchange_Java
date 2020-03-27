package com.bizzan.bitrade.dao;

import java.util.List;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.RedEnvelope;

public interface RedEnvelopeDao extends BaseDao<RedEnvelope>{
	
	RedEnvelope findByEnvelopeNo(String envelopeNo);
	
	List<RedEnvelope> findAllByMemberId(Long memberId);
	
	List<RedEnvelope> findAllByState(int state);
}
