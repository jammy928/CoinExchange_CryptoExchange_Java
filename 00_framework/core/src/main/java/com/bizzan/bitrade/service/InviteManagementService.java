package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.MemberDao;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.vo.InviteManagementVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class InviteManagementService extends BaseService{
    @Autowired
    private MemberDao dao;

    /**
     * 默认查询所有的用户
     * @return
     */
    public Page<Member> lookAll(@RequestBody InviteManagementVO inviteManagementVO) {
        Criteria<Member> releaseBalance = new Criteria<>();
        Sort sort = releaseBalance.sort("registrationTime.desc");
        // PageNum 当前页 PageSize 每页多少条
        PageRequest pageRequest = new PageRequest(inviteManagementVO.getPageNo() - 1,inviteManagementVO.getPageSize(),sort);
        return dao.findAll(releaseBalance,pageRequest);
    }

    /**
     * 根据条件查询
     * @param imVO
     * @return
     */
    public Page<Member> queryCondition(InviteManagementVO imVO) {
        Criteria<Member> criteria = new Criteria<>();
        Sort sort = criteria.sort("registrationTime.desc");
        if(StringUtils.isNotEmpty(imVO.getRealName())){
            criteria.add(Restrictions.eq("realName", imVO.getRealName(), false));
        }
        if(StringUtils.isNotEmpty(imVO.getMobilePhone())){
            criteria.add(Restrictions.eq("mobilePhone", imVO.getMobilePhone(), false));
        }
        if(StringUtils.isNotEmpty(imVO.getEmail())){
            criteria.add(Restrictions.eq("email", imVO.getEmail(), false));
        }
        PageRequest pageRequest = new PageRequest(imVO.getPageNo() - 1,imVO.getPageSize(),sort);
        return dao.findAll(criteria,pageRequest);
    }

    /**
     * 根据id查询1级2级用户
     * @param inviteManagementVO
     * @return
     */
    public Page<Member> queryId(InviteManagementVO inviteManagementVO) {
        Member member = dao.findOne(inviteManagementVO.getId());
        // 获取当前用户的id  根据id去查询邀请者ID 为这个id 的所有的数据
        List<Member> memberList = new ArrayList<>();
        List<Member> firstMemberList = dao.findAllByInviterId(member.getId());
        for (int i = 0; i < firstMemberList.size(); i++) {
            memberList.add(firstMemberList.get(i));
        }
        // 存储一级数据.
        Set set = new HashSet();
        for (int i = 0; i < firstMemberList.size(); i++) {
            set.add(firstMemberList.get(i).getId());
        }
        for (Object setId : set) {
            List<Member> list = dao.findAllByInviterId((Long) setId);
            for (int i = 0; i < list.size(); i++) {
                memberList.add(list.get(i));
            }
        }
        // 对查询出来的list进行ID倒序排序
        ListSort(memberList);
        Pageable pageable = new PageRequest(inviteManagementVO.getPageNumber() -1, inviteManagementVO.getPageSize());
        PageImpl<Member> pageData = getPageData(pageable, memberList);

        return pageData;
    }

    /**
     * list 分页工具
     * @param pageable
     * @param maps
     * @return
     */
    private PageImpl<Member> getPageData(Pageable pageable, List<Member> maps) {
        PageImpl<Member> page = null;
        try {
            // 每页起始
            int startValue = pageable.getPageNumber() * pageable.getPageSize();
            // 每页终止
            int endValue = (pageable.getPageNumber() + 1) * pageable.getPageSize();
            if (endValue > maps.size()) {
                endValue = maps.size();
            }
            List<Member> dataList = Lists.newArrayList();
            for (int i = startValue; i <endValue ; i++) {
                dataList.add(maps.get(i));
            }
            Long count = 0L;
            if(dataList != null && dataList.size()>0){
                count = Long.valueOf(maps.size());
            }
            page = new PageImpl<>(dataList,pageable,count);
        } catch (Exception e) {
            log.info("分页异常了 getPageData={}",e);
        }
        return page;
    }

    /**
     * 针对list集合排序
     * @param list
     */
    private static void ListSort(List<Member> list) {
        Collections.sort(list, (o1, o2) -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date dt1 = format.parse(String.valueOf(o1.getRegistrationTime()));
                Date dt2 = format.parse(String.valueOf(o2.getRegistrationTime()));
                if (dt1.getTime() < dt2.getTime()) {
                    return 1;
                } else if (dt1.getTime() > dt2.getTime()) {
                    return -1;
                } else {
                    return 0;
                }
            } catch (Exception e) {
                log.info("倒序时间错误 ListSort={}",e);
            }
            return 0;
        });
    }
}
