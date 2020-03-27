package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.dao.MemberBonusDao;
import com.bizzan.bitrade.dto.MemberBonusDTO;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @author: GuoShuai
 * @date: create in 16:22 2018/6/30
 * @Modified:
 */
@Service
public class MemberBonusService extends BaseService<MemberBonusDTO> {
    
    @Autowired
    private MemberBonusDao memberBonusDao;
    
    public List<MemberBonusDTO> getBonusByMemberId(long memberId){
        return memberBonusDao.getBonusByMemberId(memberId);
    }
    
    public BigDecimal getBonusAmountByMemberId(long memberId){
        return memberBonusDao.getBonusAmountByMemberId(memberId);
    }

    public MemberBonusDTO save(MemberBonusDTO memberBonusDTO) {
        return memberBonusDao.save(memberBonusDTO);
    }
    //通过memberId进行查询并分页
    public Page<MemberBonusDTO> getBonusByMemberIdPage(long memberId, Integer pageNo, Integer pageSize) {
        Sort orders = Criteria.sortStatic("id.desc");
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        Criteria<MemberBonusDTO> criteria = new Criteria<MemberBonusDTO>();
        criteria.add(Restrictions.eq("memberId",memberId,false));
        return memberBonusDao.findAll(criteria,pageRequest);
    }

    public  Page<MemberBonusDTO> getMemberBounsPage(Integer pageNo,Integer pageSize){
        Sort orders =Criteria.sortStatic("id.desc");
        PageRequest pageRequest=new PageRequest(pageNo,pageSize,orders);
        return  memberBonusDao.findAll(pageRequest);
    }
}
