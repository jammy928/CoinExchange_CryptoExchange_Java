package com.bizzan.bitrade.dao;

import java.util.List;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.RedEnvelopeDetail;

public interface RedEnvelopeDetailDao  extends BaseDao<RedEnvelopeDetail>{
	
	List<RedEnvelopeDetail> findAllByEnvelopeIdAndMemberId(Long envelopeId, Long memberId);
	
	List<RedEnvelopeDetail> findAllByEnvelopeId(Long envelopeId);
}
