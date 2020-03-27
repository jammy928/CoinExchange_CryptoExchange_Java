package com.bizzan.bitrade.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dao.FavorSymbolRepository;
import com.bizzan.bitrade.entity.FavorSymbol;
import com.bizzan.bitrade.util.DateUtil;

import java.util.List;

@Service
public class FavorSymbolService {
    @Autowired
    private FavorSymbolRepository favorSymbolRepository;

    /**
     * 添加自选
     * @param memberId
     * @param symbol
     * @return
     */
    public FavorSymbol add(Long memberId,String symbol){
        FavorSymbol favor = new FavorSymbol();
        favor.setMemberId(memberId);
        favor.setAddTime(DateUtil.getDateTime());
        favor.setSymbol(symbol);
        return favorSymbolRepository.save(favor);
    }

    /**
     * 删除自选
     * @param memberId
     * @param symbol
     */
    public void delete(Long memberId,String symbol){
        FavorSymbol favor = favorSymbolRepository.findByMemberIdAndSymbol(memberId,symbol);
        if(favor != null){
            favorSymbolRepository.delete(favor);
        }
    }

    /**
     * 查询会员所有的自选
     * @param memberId
     * @return
     */
    public List<FavorSymbol> findByMemberId(Long memberId){
        return favorSymbolRepository.findAllByMemberId(memberId);
    }

    /**
     * 查询某个自选
     * @param memberId
     * @param symbol
     * @return
     */
    public FavorSymbol findByMemberIdAndSymbol(Long memberId,String symbol){
        return favorSymbolRepository.findByMemberIdAndSymbol(memberId,symbol);
    }
}
