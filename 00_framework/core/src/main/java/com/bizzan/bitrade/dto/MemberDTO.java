package com.bizzan.bitrade.dto;

import lombok.Data;

import java.util.List;

import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;

@Data
public class MemberDTO {

    private Member member ;

    private List<MemberWallet> list ;

}
