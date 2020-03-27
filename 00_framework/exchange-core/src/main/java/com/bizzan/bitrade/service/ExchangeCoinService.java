package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.ExchangeCoinRepository;
import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.pagination.Criteria;
import com.querydsl.core.types.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Path;
import java.util.List;

@Service
public class ExchangeCoinService {
    @Autowired
    private ExchangeCoinRepository coinRepository;

    public List<ExchangeCoin> findAllEnabled() {
        Specification<ExchangeCoin> spec = (root, criteriaQuery, criteriaBuilder) -> {
            Path<String> enable = root.get("enable");
            criteriaQuery.where(criteriaBuilder.equal(enable, 1));
            return null;
        };
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "sort");
        Sort sort = new Sort(order);
        return coinRepository.findAll(spec, sort);
    }

    //获取所有可显示币种
    public List<ExchangeCoin> findAllVisible() {
        Specification<ExchangeCoin> spec = (root, criteriaQuery, criteriaBuilder) -> {
            Path<String> visible = root.get("visible");
            Path<String> enable = root.get("enable");
            criteriaQuery.where(criteriaBuilder.equal(enable, 1), criteriaBuilder.equal(visible, 1));
            return null;
        };
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "sort");
        Sort sort = new Sort(order);
        return coinRepository.findAll(spec, sort);
    }
    
    public List<ExchangeCoin> findAllByFlag(int flag) {
        Specification<ExchangeCoin> spec = (root, criteriaQuery, criteriaBuilder) -> {
            Path<String> enable = root.get("enable");
            Path<Integer> flagPath = root.get("flag");
            criteriaQuery.where(criteriaBuilder.equal(enable, 1), criteriaBuilder.equal(flagPath, flag));
            return null;
        };
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "sort");
        Sort sort = new Sort(order);
        return coinRepository.findAll(spec, sort);
    }

    public ExchangeCoin findOne(String id) {
        return coinRepository.findOne(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletes(String[] ids) {
        for (String id : ids) {
            coinRepository.delete(id);
        }
    }

    public ExchangeCoin save(ExchangeCoin exchangeCoin) {
        return coinRepository.save(exchangeCoin);
    }

    public Page<ExchangeCoin> pageQuery(int pageNo, Integer pageSize) {
        Sort orders = Criteria.sortStatic("sort");
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        return coinRepository.findAll(pageRequest);
    }

    public ExchangeCoin findBySymbol(String symbol) {
        return coinRepository.findBySymbol(symbol);
    }

    public List<ExchangeCoin> findAll() {
        return coinRepository.findAll();
    }

    public boolean isSupported(String symbol) {
    	ExchangeCoin coin = findBySymbol(symbol);
    	return coin != null && (coin.getEnable() == 1);
    }

    public Page<ExchangeCoin> findAll(Predicate predicate, Pageable pageable) {
        return coinRepository.findAll(predicate, pageable);
    }

    public List<String> getBaseSymbol() {
        return coinRepository.findBaseSymbol();
    }

    public List<String> getCoinSymbol(String baseSymbol) {
        return coinRepository.findCoinSymbol(baseSymbol);
    }

    public List<String> getAllCoin(){
        return coinRepository.findAllCoinSymbol();
    }

}
