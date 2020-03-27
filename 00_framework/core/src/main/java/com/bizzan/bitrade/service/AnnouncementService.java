package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.AnnouncementDao;
import com.bizzan.bitrade.entity.Announcement;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author GS
 * @description
 * @date 2018/3/5 15:24
 */
@Service
public class AnnouncementService extends BaseService<Announcement> {
    @Autowired
    private AnnouncementDao announcementDao;

    public Announcement save(Announcement announcement) {
        return announcementDao.save(announcement);
    }

    @Override
    public List<Announcement> findAll() {
        return announcementDao.findAll();
    }

    public Announcement findById(Long id) {
        return announcementDao.findOne(id);
    }

    public void deleteById(Long id) {
        announcementDao.delete(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] ids) {
        for (Long id : ids) {
            Announcement announcement = findById(id);
            Assert.notNull(announcement, "validate id!");
            deleteById(id);
        }
    }

    public int getMaxSort(){
        return announcementDao.findMaxSort();
    }

    public Page<Announcement> findAll(Predicate predicate, Pageable pageable) {
        return announcementDao.findAll(predicate, pageable);
    }


    /**
     * 获取公告上一条
     * @param id
     * @return
     */
    public Announcement getBack(long id, String lang){
    	Announcement back = announcementDao.getBack(id, lang);
    	// 无需内容传输
    	if(back != null) {
    		back.setContent(null);
    	}
        return back;
    }

    /**
     * 获取公告下一条
     * @param id
     * @return
     */
    public Announcement getNext(long id, String lang){
    	Announcement next = announcementDao.getNext(id, lang);
    	if(next != null) {
    		next.setContent(null);
    	}
        return next;
    }
}
