package com.bizzan.bitrade.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MemberInviteStasticRank;

@Repository
public interface MemberInviteStasticRankDao extends  BaseDao<MemberInviteStasticRank> {

	MemberInviteStasticRank findByMemberId(Long memberId);
	
	MemberInviteStasticRank findById(Long id);
	
	/**
	 * 获取最新排名
	 * @param type 0:日榜  1：周榜  2：月榜
	 * @param count 排名前N个
	 * @return
	 */
	@Query(value = "select * from member_invite_stastic_rank where type = :type order by stastic_date desc, level_one desc limit :count", nativeQuery = true)
	List<MemberInviteStasticRank> getLastedRankByType(@Param("type") int type, @Param("count") int count);
}
