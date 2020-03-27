package com.bizzan.bitrade.dao;

import java.util.List;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.OtcCoin;
import com.bizzan.bitrade.entity.PromotionCardOrder;

public interface PromotionCardOrderDao extends BaseDao<PromotionCardOrder> {
	List<PromotionCardOrder> findAllByCardIdAndMemberId(Long cardId, Long memberId);
	
	List<PromotionCardOrder> findAllByCardId(Long cardId);


	List<PromotionCardOrder> findAllByMemberIdAndIsFree(long memberId, int isFree);
}
