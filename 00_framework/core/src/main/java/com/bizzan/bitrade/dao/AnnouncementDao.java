package com.bizzan.bitrade.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Announcement;

/**
 * @author GS
 * @description
 * @date 2018/3/5 15:32
 */
public interface AnnouncementDao extends BaseDao<Announcement> {

    @Query("select max(s.sort) from Announcement s")
    int findMaxSort();


    /**
     * 获取公告上一条
     * @param annId
     * @return
     */
    @Query(value = "select * from announcement where id<:annId AND is_show=1 AND lang=:lang ORDER by id desc limit 0,1",nativeQuery = true)
    Announcement getBack(@Param("annId") long annId, @Param("lang") String lang);

    /**
     * 获取公告下一条
     * @param annId
     * @return
     */
    @Query(value = "select * from announcement where id>:annId AND is_show=1 AND lang=:lang limit 0,1",nativeQuery = true)
    Announcement getNext(@Param("annId") long annId, @Param("lang") String lang);
}
