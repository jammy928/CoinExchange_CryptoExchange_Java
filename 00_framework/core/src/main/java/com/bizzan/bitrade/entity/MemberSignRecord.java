package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author GS
 * @Description: 会员签到记录
 * @date 2018/5/410:12
 */
@Entity
@Data
@Table
public class MemberSignRecord {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "sign_id")
    private Sign sign;

    /**
     * 赠送币种
     */
    @OneToOne
    @JoinColumn(name = "coin_name")
    private Coin coin;

    /**
     * 赠送数目
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private BigDecimal amount;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime;

    public MemberSignRecord() {
    }

    public MemberSignRecord(Member member, Sign sign) {
        this.member = member;
        this.sign = sign;
        this.coin = sign.getCoin();//防止sign信息修改
        this.amount = sign.getAmount();//防止sign信息修改
    }
}
