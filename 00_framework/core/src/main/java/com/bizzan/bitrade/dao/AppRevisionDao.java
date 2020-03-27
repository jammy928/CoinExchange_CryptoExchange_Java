package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.Platform;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.AppRevision;

/**
 * @author GS
 * @Title: ${file_name}
 * @Description:
 * @date 2018/4/2416:18
 */
public interface AppRevisionDao extends BaseDao<AppRevision> {
    AppRevision findAppRevisionByPlatformOrderByIdDesc(Platform platform);
}
