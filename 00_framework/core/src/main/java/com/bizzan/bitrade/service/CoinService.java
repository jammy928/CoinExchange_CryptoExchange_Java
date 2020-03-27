package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.dao.CoinDao;
import com.bizzan.bitrade.dao.OtcCoinDao;
import com.bizzan.bitrade.dto.CoinDTO;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.OtcCoin;
import com.bizzan.bitrade.entity.QCoin;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bizzan.bitrade.constant.BooleanEnum.IS_FALSE;
import static com.bizzan.bitrade.constant.BooleanEnum.IS_TRUE;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GS
 * @description
 * @date 2017/12/29 14:50
 */
@Service
public class CoinService extends BaseService {
    @Autowired
    private CoinDao coinDao;
    @Autowired
    private OtcCoinDao otcCoinDao;

    /**
     * 条件查询对象 pageNo pageSize 同时传时分页
     *
     * @param booleanExpressionList
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<Coin> query(List<BooleanExpression> booleanExpressionList, Integer pageNo, Integer pageSize) {
        List<Coin> list;
        JPAQuery<Coin> jpaQuery = queryFactory.selectFrom(QCoin.coin);
        if (booleanExpressionList != null) {
            jpaQuery.where(booleanExpressionList.toArray(new BooleanExpression[booleanExpressionList.size()]));
        }
        if (pageNo != null && pageSize != null) {
            list = jpaQuery.offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
        } else {
            list = jpaQuery.fetch();
        }
        return new PageResult<>(list, jpaQuery.fetchCount());//添加总条数
    }

    public Coin findOne(String name) {
        return coinDao.findOne(name);
    }

    public Coin findByUnit(String unit) {
        return coinDao.findByUnit(unit);
    }

    public Coin save(Coin coin) {
        return coinDao.save(coin);
    }

    @Override
    public List<Coin> findAll() {
        return coinDao.findAll();
    }

    public List<Coin> findAllCoinByOtc() {
        List<String> supportUnits = otcCoinDao.findAll().stream().map(x -> x.getUnit()).collect(Collectors.toList());
        if (supportUnits.size() > 0) {
            return coinDao.findAllByOtc(supportUnits);
        } else {
            return null;
        }
    }

    public Page<Coin> pageQuery(Integer pageNo, Integer pageSize) {
        //排序方式 (需要倒序 这样    Criteria.sort("id","createTime.desc") ) //参数实体类为字段名
        Sort orders = Criteria.sortStatic("sort");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo, pageSize, orders);
        //查询条件
        Criteria<Coin> specification = new Criteria<Coin>();
        return coinDao.findAll(specification, pageRequest);
    }

    public List<Coin> findAllCanWithDraw() {
        return coinDao.findAllByCanWithdrawAndStatusAndHasLegal(IS_TRUE, CommonStatus.NORMAL, false);
    }

    public List<Coin> findAllStatus() {
        return coinDao.findAllByStatusAndHasLegal(CommonStatus.NORMAL, false);
    }


    public void deleteOne(String name) {
        coinDao.delete(name);
    }

    /**
     * 设置平台币
     *
     * @param coin
     */
    @Transactional(rollbackFor = Exception.class)
    public void setPlatformCoin(Coin coin) {
        List<Coin> list = coinDao.findAll();
        list.stream().filter(x ->
                !x.getName().equals(coin.getName())
        ).forEach(x -> {
            x.setIsPlatformCoin(BooleanEnum.IS_FALSE);
            coinDao.save(x);
        });
        coin.setIsPlatformCoin(IS_TRUE);
        coinDao.saveAndFlush(coin);
        OtcCoin otcCoin = otcCoinDao.findOtcCoinByUnit(coin.getUnit());
        if (otcCoin != null) {
            otcCoin.setIsPlatformCoin(IS_TRUE);
            otcCoinDao.saveAndFlush(otcCoin);
        }
        List<OtcCoin> list1 = otcCoinDao.findAll();
        list1.stream().filter(x ->
                !x.getUnit().equals(coin.getUnit())
        ).forEach(x -> {
            x.setIsPlatformCoin(BooleanEnum.IS_FALSE);
            otcCoinDao.save(x);
        });
    }

    public Coin queryPlatformCoin() {
        return coinDao.findCoinByIsPlatformCoin(IS_TRUE);
    }

    /**
     * @Description: 查询所有合法币种
     * @author GS
     */
    public List<Coin> findLegalAll() {
        return (List<Coin>) coinDao.findAll(QCoin.coin.hasLegal.eq(true));
    }

    public Page<Coin> findAll(Predicate predicate, Pageable pageable) {
        return coinDao.findAll(predicate, pageable);
    }

    public Page findLegalCoinPage(PageModel pageModel) {
        BooleanExpression eq = QCoin.coin.hasLegal.eq(true);
        return coinDao.findAll(eq, pageModel.getPageable());
    }

    public List<String> getAllCoinName() {
        List<String> list = coinDao.findAllName();
        return list;
    }

    public List<CoinDTO> getAllCoinNameAndUnit() {
        List<CoinDTO> allNameAndUnit = coinDao.findAllNameAndUnit();
        return allNameAndUnit;
    }

    public List<String> getAllCoinNameLegal() {
        return coinDao.findAllCoinNameLegal();
    }

    public List<String> findAllRpcUnit() {
        return coinDao.findAllRpcUnit();
    }

    /**
     * 设置平台币
     *
     * @param coin 需要设置的币种(持久态)
     */
    @Transactional(rollbackFor = Exception.class)
    public void setPlatform(Coin coin) {
        //取消其他平台币
        List<Coin> coins = coinDao.findAllByIsPlatformCoin(IS_TRUE);
        coins.forEach(x -> x.setIsPlatformCoin(IS_FALSE));
        //设置传入币为平台币
        coin.setIsPlatformCoin(IS_TRUE);
    }

}
