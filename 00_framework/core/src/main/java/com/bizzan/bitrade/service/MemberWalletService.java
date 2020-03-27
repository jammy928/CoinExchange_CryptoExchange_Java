package com.bizzan.bitrade.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.dao.CoinDao;
import com.bizzan.bitrade.dao.MemberDao;
import com.bizzan.bitrade.dao.MemberDepositDao;
import com.bizzan.bitrade.dao.MemberWalletDao;
import com.bizzan.bitrade.dto.MemberWalletDTO;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberDeposit;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.Order;
import com.bizzan.bitrade.entity.OtcCoin;
import com.bizzan.bitrade.entity.QMember;
import com.bizzan.bitrade.entity.QMemberWallet;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.BigDecimalUtils;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberWalletService extends BaseService {
	@Autowired
    private SMSProvider smsProvider;
	@Autowired
    private MemberDao memberDao;
    @Autowired
    private MemberWalletDao memberWalletDao;
    @Autowired
    private CoinDao coinDao;
    @Autowired
    private MemberTransactionService transactionService;
    @Autowired
    private MemberDepositDao depositDao;
    @Autowired(required=false)
    private ESUtils esUtils;
    
    public MemberWallet save(MemberWallet wallet) {
        return memberWalletDao.saveAndFlush(wallet);
    }

    /**
     * 获取钱包
     *
     * @param coin     otc币种
     * @param memberId
     * @return
     */
    public MemberWallet findByOtcCoinAndMemberId(OtcCoin coin, long memberId) {
        Coin coin1 = coinDao.findByUnit(coin.getUnit());
        return memberWalletDao.findByCoinAndMemberId(coin1, memberId);
    }

    /**
     * 钱包充值
     *
     * @param wallet
     * @param amount
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageResult recharge(MemberWallet wallet, BigDecimal amount) {
        if (wallet == null) {
            return new MessageResult(500, "wallet cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new MessageResult(500, "amount must large then 0");
        }
        int result = memberWalletDao.increaseBalance(wallet.getId(), amount);
        if (result > 0) {
            MemberTransaction transaction = new MemberTransaction();
            transaction.setAmount(amount);
            transaction.setSymbol(wallet.getCoin().getUnit());
            transaction.setAddress(wallet.getAddress());
            transaction.setMemberId(wallet.getMemberId());
            transaction.setType(TransactionType.RECHARGE);
            transaction.setFee(BigDecimal.ZERO);
            transactionService.save(transaction);
            //增加记录
            return new MessageResult(0, "success");
        } else {
            return new MessageResult(500, "recharge failed");
        }
    }

    /**
     * 钱包充值
     *
     * @param coin    币种名称
     * @param address 地址
     * @param amount  金额
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageResult recharge(Coin coin, String address, BigDecimal amount, String txid) {
        MemberWallet wallet = findByCoinAndAddress(coin, address);
        if (wallet == null) {
            return new MessageResult(500, "wallet cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new MessageResult(500, "amount must large then 0");
        }
        MemberDeposit deposit = new MemberDeposit();
        deposit.setAddress(address);
        deposit.setAmount(amount);
        deposit.setMemberId(wallet.getMemberId());
        deposit.setTxid(txid);
        deposit.setUnit(wallet.getCoin().getUnit());
        depositDao.save(deposit);

        wallet.setBalance(wallet.getBalance().add(amount)); // 为用户增加余额
        
        MemberTransaction transaction = new MemberTransaction();
        transaction.setAmount(amount);
        transaction.setSymbol(wallet.getCoin().getUnit());
        transaction.setAddress(wallet.getAddress());
        transaction.setMemberId(wallet.getMemberId());
        transaction.setType(TransactionType.RECHARGE);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setDiscountFee("0");
        transaction.setRealFee("0");
        transaction.setCreateTime(new Date());

        transaction = transactionService.save(transaction);

        Member mRes = memberDao.findOne(wallet.getMemberId());
        if(mRes != null ) {
        	try {
				smsProvider.sendCustomMessage(mRes.getMobilePhone(), "尊敬的用户：恭喜您充值"+ wallet.getCoin().getUnit() + "成功，充值数量为：" + amount.stripTrailingZeros().toPlainString());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return new MessageResult(0, "success");
    }
    
    /**
     * 钱包充值(EOS地址类型）
     *
     * @param coin    币种名称
     * @param address 地址
     * @param amount  金额
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageResult recharge2(Long userId, Coin coin, String address, BigDecimal amount, String txid) {
        MemberWallet wallet = findByCoinAndMemberId(coin, userId);
        if (wallet == null) {
            return new MessageResult(500, "wallet cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new MessageResult(500, "amount must large then 0");
        }
        MemberDeposit deposit = new MemberDeposit();
        deposit.setAddress(address);
        deposit.setAmount(amount);
        deposit.setMemberId(wallet.getMemberId());
        deposit.setTxid(txid);
        deposit.setUnit(wallet.getCoin().getUnit());
        depositDao.save(deposit);

        wallet.setBalance(wallet.getBalance().add(amount));
        MemberTransaction transaction = new MemberTransaction();
        transaction.setAmount(amount);
        transaction.setSymbol(wallet.getCoin().getUnit());
        transaction.setAddress(address);
        transaction.setMemberId(wallet.getMemberId());
        transaction.setType(TransactionType.RECHARGE);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setDiscountFee("0");
        transaction.setRealFee("0");
        transaction.setCreateTime(new Date());

        transaction = transactionService.save(transaction);

        Member mRes = memberDao.findOne(wallet.getMemberId());
        if(mRes != null ) {
        	try {
				smsProvider.sendCustomMessage(mRes.getMobilePhone(), "尊敬的用户：恭喜您充值"+ wallet.getCoin().getUnit() + "成功，充值数量为：" + amount.stripTrailingZeros().toPlainString());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return new MessageResult(0, "success");
    }


    /**
     * 根据币种和钱包地址获取钱包
     *
     * @param coin
     * @param address
     * @return
     */
    public MemberWallet findByCoinAndAddress(Coin coin, String address) {
        return memberWalletDao.findByCoinAndAddress(coin, address);
    }

    /**
     * 根据币种和用户ID获取钱包
     *
     * @param coin
     * @param member
     * @return
     */
    public MemberWallet findByCoinAndMember(Coin coin, Member member) {
        return memberWalletDao.findByCoinAndMemberId(coin, member.getId());
    }

    public MemberWallet findByCoinUnitAndMemberId(String coinUnit, Long memberId) {
        Coin coin = coinDao.findByUnit(coinUnit);
        return memberWalletDao.findByCoinAndMemberId(coin, memberId);
    }

    public MemberWallet findByCoinAndMemberId(Coin coin, Long memberId) {
        return memberWalletDao.findByCoinAndMemberId(coin, memberId);
    }

    /**
     * 根据用户查找所有钱包
     *
     * @param member
     * @return
     */
    public List<MemberWallet> findAllByMemberId(Member member) {
        return memberWalletDao.findAllByMemberId(member.getId());
    }

    public List<MemberWallet> findAllByMemberId(Long memberId) {
        return memberWalletDao.findAllByMemberId(memberId);
    }

    /**
     * 冻结钱包
     *
     * @param memberWallet
     * @param amount
     * @return
     */
    public MessageResult freezeBalance(MemberWallet memberWallet, BigDecimal amount) {
        int ret = memberWalletDao.freezeBalance(memberWallet.getId(), amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            return MessageResult.error("Information Expired");
        }
    }

    /**
     * 解冻钱包
     *
     * @param memberWallet
     * @param amount
     * @return
     */
    public MessageResult thawBalance(MemberWallet memberWallet, BigDecimal amount) {
        int ret = memberWalletDao.thawBalance(memberWallet.getId(), amount);
        if (ret > 0) {
            return MessageResult.success();
        } else {
            log.info("====order cancel=====订单取消异常（amount）："+amount);
            return MessageResult.error("Information Expired");
        }
    }

    /**
     * 放行更改双方钱包余额
     *
     * @param order
     * @param ret
     * @throws InformationExpiredException
     */
    public void transfer(Order order, int ret) throws InformationExpiredException {
        if (ret == 1) {
            MemberWallet customerWallet = findByOtcCoinAndMemberId(order.getCoin(), order.getCustomerId());
            int is = memberWalletDao.decreaseFrozen(customerWallet.getId(), order.getNumber());
            if (is > 0) {
                MemberWallet memberWallet = findByOtcCoinAndMemberId(order.getCoin(), order.getMemberId());
                int a = memberWalletDao.increaseBalance(memberWallet.getId(), BigDecimalUtils.sub(order.getNumber(), order.getCommission()));
                if (a <= 0) {
                    throw new InformationExpiredException("Information Expired");
                }
            } else {
                throw new InformationExpiredException("Information Expired");
            }
        } else {
            MemberWallet customerWallet = findByOtcCoinAndMemberId(order.getCoin(), order.getMemberId());
            int is = memberWalletDao.decreaseFrozen(customerWallet.getId(), BigDecimalUtils.add(order.getNumber(), order.getCommission()));
            if (is > 0) {
                MemberWallet memberWallet = findByOtcCoinAndMemberId(order.getCoin(), order.getCustomerId());
                int a = memberWalletDao.increaseBalance(memberWallet.getId(), order.getNumber());
                if (a <= 0) {
                    throw new InformationExpiredException("Information Expired");
                }
            } else {
                throw new InformationExpiredException("Information Expired");
            }
        }

    }



    /* */

    /**
     * 放行更改双方钱包余额
     *
     * @param order
     * @param ret
     * @throws InformationExpiredException
     */
    public void transferAdmin(Order order, int ret) throws InformationExpiredException {
        if (ret == 1 || ret == 4) {
            trancerDetail(order, order.getCustomerId(), order.getMemberId());
        } else {
            trancerDetail(order, order.getMemberId(), order.getCustomerId());

        }

    }


    private void trancerDetail(Order order, long sellerId, long buyerId) throws InformationExpiredException {
        MemberWallet customerWallet = findByOtcCoinAndMemberId(order.getCoin(), sellerId);
        //卖币者，买币者要处理的金额
        BigDecimal sellerAmount, buyerAmount;
        if (order.getMemberId() == sellerId) {
            sellerAmount = BigDecimalUtils.add(order.getNumber(), order.getCommission());
            buyerAmount = order.getNumber();
        } else {
            sellerAmount = order.getNumber();
            buyerAmount = order.getNumber().subtract(order.getCommission());
        }
        int is = memberWalletDao.decreaseFrozen(customerWallet.getId(), sellerAmount);
        if (is > 0) {
            MemberWallet memberWallet = findByOtcCoinAndMemberId(order.getCoin(), buyerId);
            int a = memberWalletDao.increaseBalance(memberWallet.getId(), buyerAmount);
            if (a <= 0) {
                throw new InformationExpiredException("Information Expired");
            }
        } else {
            throw new InformationExpiredException("Information Expired");
        }
    }

    public int deductBalance(MemberWallet memberWallet, BigDecimal amount) {
        return memberWalletDao.decreaseBalance(memberWallet.getId(), amount);
    }

    @Override
    public List<MemberWallet> findAll() {
        return memberWalletDao.findAll();
    }

    public List<MemberWallet> findAllByCoin(Coin coin) {
        return memberWalletDao.findAllByCoin(coin);
    }

    /**
     * 锁定钱包
     *
     * @param uid
     * @param unit
     * @return
     */
    @Transactional
    public boolean lockWallet(Long uid, String unit) {
        MemberWallet wallet = findByCoinUnitAndMemberId(unit, uid);
        if (wallet != null && wallet.getIsLock() == BooleanEnum.IS_FALSE) {
            wallet.setIsLock(BooleanEnum.IS_TRUE);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 解锁钱包
     *
     * @param uid
     * @param unit
     * @return
     */
    @Transactional
    public boolean unlockWallet(Long uid, String unit) {
        MemberWallet wallet = findByCoinUnitAndMemberId(unit, uid);
        if (wallet != null && wallet.getIsLock() == BooleanEnum.IS_TRUE) {
            wallet.setIsLock(BooleanEnum.IS_FALSE);
            return true;
        } else {
            return false;
        }
    }

    public MemberWallet findOneByCoinNameAndMemberId(String coinName, long memberId) {
        BooleanExpression and = QMemberWallet.memberWallet.coin.name.eq(coinName)
                .and(QMemberWallet.memberWallet.memberId.eq(memberId));
        return memberWalletDao.findOne(and);
    }

    public Page<MemberWalletDTO> joinFind(List<Predicate> predicates,QMember qMember ,QMemberWallet qMemberWallet,PageModel pageModel) {
        List<OrderSpecifier> orderSpecifiers = pageModel.getOrderSpecifiers();
        predicates.add(qMember.id.eq(qMemberWallet.memberId));
        JPAQuery<MemberWalletDTO> query = queryFactory.select(
                        Projections.fields(MemberWalletDTO.class, qMemberWallet.id.as("id"),qMemberWallet.memberId.as("memberId") ,qMember.username,qMember.realName.as("realName"),
                        qMember.email,qMember.mobilePhone.as("mobilePhone"),qMemberWallet.balance,qMemberWallet.address,qMemberWallet.coin.unit
                        ,qMemberWallet.frozenBalance.as("frozenBalance"),qMemberWallet.balance.add(qMemberWallet.frozenBalance).as("allBalance"))).from(QMember.member,QMemberWallet.memberWallet).where(predicates.toArray(new Predicate[predicates.size()]))
                        .orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]));
        List<MemberWalletDTO> content = query.offset((pageModel.getPageNo()-1)*pageModel.getPageSize()).limit(pageModel.getPageSize()).fetch();
        long total = query.fetchCount();
        return new PageImpl<>(content, pageModel.getPageable(), total);
    }

    public BigDecimal getAllBalance(String coinName){
        return memberWalletDao.getWalletAllBalance(coinName);
    }

    public MemberDeposit findDeposit(String address,String txid){
        return depositDao.findByAddressAndTxid(address,txid);
    }
    
    public MemberDeposit findDepositByTxid(String txid){
        return depositDao.findByTxid(txid);
    }

    public void decreaseFrozen(Long walletId,BigDecimal amount){
        memberWalletDao.decreaseFrozen(walletId,amount);
    }

    public void increaseBalance(Long walletId,BigDecimal amount){
        memberWalletDao.increaseBalance(walletId,amount);
    }
    
    public int unfreezeLess(){
        return memberWalletDao.unfreezeLess();
    }
    
    public int unfreezeMore(){
        return memberWalletDao.unfreezeMore();
    }
    
    
//    public int initSuperPaterner(long memberId){
//        return memberWalletDao.initSuperPaterner(memberId);
//    }
    
    public int dropWeekTable(int weekDay){
        return memberWalletDao.dropWeekTable(weekDay);
    }
    
    public int createWeekTable(int weekDay){
        return memberWalletDao.createWeekTable(weekDay);
    }

    public BigDecimal getWalletBalanceAmount(String coinId,int week){
        return memberWalletDao.getWalletBalanceAmount(coinId,week);
    }

    public List<MemberWallet> geteveryBHB(String coinName,int week){
        return memberWalletDao.geteveryBHB(coinName,week);
    }


    //更新团队钱包
    public int updateTeamWallet(BigDecimal teamBalance,long teamId){
        return memberWalletDao.updateTeamWallet(teamBalance,teamId);
    }

    /**
     * 根据 coinId和会员ID查询会员账户信息
     * @param coinId
     * @param memberId
     * @return
     */
    public MemberWallet getMemberWalletByCoinAndMemberId(String coinId, long memberId) {
        return memberWalletDao.getMemberWalletByCoinAndMemberId(coinId,memberId);
    }

    /**
     * 根据用户ID和coinID更新用户钱包
     *
     */
    @Transactional
    public int updateByMemberIdAndCoinId(long memberId,String coinId,BigDecimal balance){
        return memberWalletDao.updateByMemberIdAndCoinId(memberId,coinId,balance);
    }

    /**
     * 增加用户BHB钱包余额
     * @param mineAmount
     * @param memberId
     * @return
     */
    public int increaseBalanceForBHB(BigDecimal mineAmount,Long memberId) {
       return  memberWalletDao.increaseBalanceForBHB(mineAmount,memberId);
    }


    /**
     * 查询待释放BHB大于500的
     */
    public List<MemberWallet> findUnfreezeGTE(){
        return  memberWalletDao.findUnfreezeGTE();
    }
    /**
     * 查询待释放BHB大于500的
     */
    public List<MemberWallet> findUnfreezeLTE(){
        return  memberWalletDao.findUnfreezeLTE();
    }


    public int updateBalanceByIdAndAmount (long id,double amount){
        return memberWalletDao.updateBalanceByIdAndAmount(id,amount);
    }

    /**
     * 增加冻结资产（与余额无关）
     * @param id
     * @param amount
     */
	public void increaseFrozen(Long id, BigDecimal amount) {
		memberWalletDao.increaseFrozen(id, amount);
	}
}
