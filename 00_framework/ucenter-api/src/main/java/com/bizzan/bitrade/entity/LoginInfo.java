package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.MemberLevelEnum;
import com.bizzan.bitrade.entity.Country;
import com.bizzan.bitrade.entity.Location;
import com.bizzan.bitrade.entity.Member;

import lombok.Builder;
import lombok.Data;

/**
 * @author GS
 * @date 2018年01月31日
 */
@Data
@Builder
public class LoginInfo {
    private String username;
    private Location location;
    private MemberLevelEnum memberLevel;
    private String token;
    private String realName;
    private Country country;
    private String avatar;
    private String promotionCode;
    private long id;
    private int loginCount;
    private String superPartner;
    /**
     * 推广地址前缀
     */
    private String promotionPrefix;

    /**
     * 签到能力
     */
    private Boolean signInAbility;
    
    private int firstLevel = 0; // 一级邀请好友数量
    private int secondLevel = 0; // 二级邀请好友数量
    private int thirdLevel = 0; // 三级邀请好友数量
    
    /**
     * 是否存在签到活动
     */
    private Boolean signInActivity;
    private  String memberRate ;

    public static LoginInfo getLoginInfo(Member member, String token, Boolean signInActivity, String prefix) {
        return LoginInfo.builder().location(member.getLocation())
                .memberLevel(member.getMemberLevel())
                .username(member.getUsername())
                .token(token)
                .realName(member.getRealName())
                .country(member.getCountry())
                .avatar(member.getAvatar())
                .promotionCode(member.getPromotionCode())
                .id(member.getId())
                .loginCount(member.getLoginCount())
                .superPartner(member.getSuperPartner())
                .promotionPrefix(prefix)
                .signInAbility(member.getSignInAbility())
                .signInActivity(signInActivity)
                .firstLevel(member.getFirstLevel())
                .secondLevel(member.getSecondLevel())
                .thirdLevel(member.getThirdLevel())
                .build();
    }
}
