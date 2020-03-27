package com.bizzan.bitrade.service;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizzan.bitrade.dao.AdminDao;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.service.Base.TopBaseService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GS
 * @date 2017年12月18日
 */
@Service
public class AdminService extends TopBaseService<Admin, AdminDao> {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Autowired
    public void setDao(AdminDao dao) {
        super.setDao(dao);
    }


    public Admin login(String username, String password) {
        Admin admin = dao.findAdminByUsernameAndPassword(username, password);
        return admin;
    }

    @Transactional(rollbackFor = Exception.class)
    public int update(Date date, String ip, Long memberId) {
        return dao.updateAdminLastTimeAndIp(date, ip, memberId);
    }

    public Admin findOne(Long id) {
        return dao.findOne(id);
    }

    public Map findAdminDetail(Long id) {
        String sql = "select a.id,a.role_id roleId,a.department_id departmentId,a.real_name realName,a.avatar,a.email,a.enable,a.mobile_phone mobilePhone,a.qq,a.username, " +
                "d.name as 'departmentName',r.role from admin a LEFT join department d on a.department_id=d.id LEFT JOIN admin_role r on a.role_id=r.id WHERE a.id=:adminId ";
        Query query = em.createNativeQuery(sql);
        //设置结果转成Map类型
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        Object object = query.setParameter("adminId", id).getSingleResult();
        Map map = (HashMap) object;
        return map;
    }

    public Admin saveAdmin(Admin admin) {
        return dao.save(admin);
    }

   /* public List<Admin> findAll(String searchKey,int pageNo,int pageSize) throws IllegalArgumentException {
        List<Predicate> predicate = new ArrayList<Predicate>();
        QAdmin admin  = QAdmin.admin;
        predicate.add(QAdmin.admin.username.notEqualsIgnoreCase("root"))
        if(StringUtils.isNotBlank(searchKey)){
            predicate.add(admin.email.like(searchKey)
                    .or(admin.realName.like(searchKey))
                    .or(admin.mobilePhone.like(searchKey))
                    .or(admin.username.like(searchKey))
            );
        }
        List<Admin> list = queryFactory.selectFrom(admin)
                .where(predicate.toArray(new Predicate[predicate.size()]))
                .orderBy(admin.id.desc())
                .offset(pageNo*pageSize)
                .limit(pageSize)
                .fetch();
        return list;
    }*/

    public Admin findByUsername(String username) {
        return dao.findByUsername(username);
    }

    /**
     * 根据用户所属角色删
     *
     * @param roleId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long roleId) {
        dao.deleteBatch(roleId);
    }

    /**
     * 根据用户id删
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] ids) {
        for (long id : ids) {
            delete(id);
        }
    }

    public Page<Admin> findAll(com.querydsl.core.types.Predicate predicate, Pageable pageable) {
        return dao.findAll(predicate, pageable);
    }

}
