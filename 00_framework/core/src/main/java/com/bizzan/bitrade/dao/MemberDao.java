package com.bizzan.bitrade.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Member;

import java.util.Date;
import java.util.List;

public interface MemberDao extends BaseDao<Member> {

    List<Member> getAllByEmailEquals(String email);

    List<Member> getAllByUsernameEquals(String username);

    List<Member> getAllByMobilePhoneEquals(String phone);

    Member findByUsername(String username);

    Member findMemberByTokenAndTokenExpireTimeAfter(String token, Date date);

    Member findMemberByToken(String token);

    Member findMemberByMobilePhoneOrEmail(String phone, String email);

    int countByRegistrationTimeBetween(Date startTime, Date endTime);

    Member findMemberByPromotionCode(String code);

    Member findMemberByEmail(String email);

    Member findMemberByMobilePhone(String mobilePhone);

    List<Member> findAllByInviterId(Long id);

    /*@Query("select new com.bizzan.bitrade.dto.MemberDTO(member,memberWallet) from")*/

    @Query(value = "select m.username from member m where m.id = :id", nativeQuery = true)
    String findUserNameById(@Param("id") Long id);

    @Modifying
    @Query(value = "update Member set signInAbility = true ")
    void resetSignIn();


    @Query(value = "update Member set certified_business_status = :status where id in (:idList) and certified_business_status=2")
    void updateCertifiedBusinessStatusByIdList(@Param("idList")List<Long> idList, @Param("status")CertifiedBusinessStatus status);

    @Query(value ="select count(id) from member where date_format(registration_time,'%Y-%m-%d') = :date",nativeQuery = true)
    int getRegistrationNum(@Param("date")String date);

    @Query(value ="select count(id) from member where date_format(certified_business_check_time,'%Y-%m-%d') = :date",nativeQuery = true)
    int getBussinessNum(@Param("date")String date);

    //以前没有application_time,若以此方法，需手动更新 在添加application_time字段之前的会员的实名通过时间
    /*
        update member a , member_application b
        set a.application_time = b.update_time
        where b.audit_status = 2 and a.application_time is NULL
        and a.id = b.member_id;
     */
    /*@Query(value ="select count(id) from member where date_format(application_time,'%Y-%m-%d') = :date",nativeQuery = true)
    int getApplicationNum(@Param("date")String date);*/

    @Query(value ="select count(a.id) from member a , member_application b where a.id = b.member_id and b.audit_status = 2 and date_format(b.update_time,'%Y-%m-%d') = :date",nativeQuery = true)
    int getApplicationNum(@Param("date")String date);

    @Query("select min(a.registrationTime) as date from Member a")
    Date getStartRegistrationDate();
    
    @Query("select max(a.id) as id from Member a")
    Long getMaxId();

    List<Member> getAllByPromotionCodeEquals(String promotion);
}
