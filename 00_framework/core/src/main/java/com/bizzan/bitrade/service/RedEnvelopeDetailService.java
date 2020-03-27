package com.bizzan.bitrade.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dao.RedEnvelopeDetailDao;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.entity.RedEnvelopeDetail;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;

@Service
public class RedEnvelopeDetailService extends BaseService{
	@Autowired
	private RedEnvelopeDetailDao redEnvelopeDetailDao;
	

	public List<RedEnvelopeDetail> findByEnvelopeIdAndMemberId(Long envelopeId, Long memberId){
		return redEnvelopeDetailDao.findAllByEnvelopeIdAndMemberId(envelopeId, memberId);
	}
	
	public List<RedEnvelopeDetail> findAllByEnvelopeId(Long envelopeId) {
		return redEnvelopeDetailDao.findAllByEnvelopeId(envelopeId);
	}
	
	public RedEnvelopeDetail findOne(Long id) {
		return redEnvelopeDetailDao.findOne(id);
	}
	
    public RedEnvelopeDetail save(RedEnvelopeDetail detail) {
        return redEnvelopeDetailDao.save(detail);
    }

    public RedEnvelopeDetail saveAndFlush(RedEnvelopeDetail detail) {
        return redEnvelopeDetailDao.saveAndFlush(detail);
    }
    
    public RedEnvelopeDetail findById(Long id) {
        return redEnvelopeDetailDao.findOne(id);
    }
    
    public Page<RedEnvelopeDetail> findAll(Predicate predicate, Pageable pageable){
    	return redEnvelopeDetailDao.findAll(predicate, pageable);
    }
    
    public Page<RedEnvelopeDetail> findByEnvelope(Long envelopeId, int pageNo, int pageSize){
    	//排序方式 (需要倒序 这样    Criteria.sort("id","createTime.desc") ) //参数实体类为字段名
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        //查询条件
        Criteria<RedEnvelopeDetail> specification = new Criteria<RedEnvelopeDetail>();
        specification.add(Restrictions.eq("envelopeId", envelopeId, false));
        return redEnvelopeDetailDao.findAll(specification, pageRequest);
    }
}
