package com.bizzan.bc.wallet.component;

import com.bizzan.bc.wallet.component.Watcher;
import com.bizzan.bc.wallet.entity.Deposit;
import com.bizzan.bc.wallet.event.DepositEvent;
import com.bizzan.bc.wallet.service.AccountService;
import com.bizzan.bc.wallet.service.EthService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EthWatcher extends Watcher{
    private Logger logger = LoggerFactory.getLogger(EthWatcher.class);
    @Autowired
    private Web3j web3j;
    @Autowired
    private EthService ethService;
    @Autowired
    private AccountService accountService;

    @Autowired
    private DepositEvent depositEvent;

    @Override
    public List<Deposit>  replayBlock(Long startBlockNumber, Long endBlockNumber) {
        List<Deposit> deposits = new ArrayList<>();
        try {
            for (Long i = startBlockNumber; i <= endBlockNumber; i++) {
                EthBlock block = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(i), true).send();

                block.getBlock().getTransactions().stream().forEach(transactionResult -> {
                    EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
                    Transaction transaction = transactionObject.get();
                    if (StringUtils.isNotEmpty(transaction.getTo())
                            && accountService.isAddressExist(transaction.getTo())
                            && !transaction.getFrom().equalsIgnoreCase(getCoin().getIgnoreFromAddress())) {
                        Deposit deposit = new Deposit();
                        deposit.setTxid(transaction.getHash());
                        deposit.setBlockHeight(transaction.getBlockNumber().longValue());
                        deposit.setBlockHash(transaction.getBlockHash());
                        deposit.setAmount(Convert.fromWei(transaction.getValue().toString(), Convert.Unit.ETHER));
                        deposit.setAddress(transaction.getTo());
                        deposits.add(deposit);
                        logger.info("received coin {} at height {}", transaction.getValue(), transaction.getBlockNumber());
                        //同步余额
                        try {
                            ethService.syncAddressBalance(deposit.getAddress());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //如果是地址簿里转出去的地址，需要同步余额
                    if (StringUtils.isNotEmpty(transaction.getTo()) && accountService.isAddressExist(transaction.getFrom())) {
                        logger.info("sync address:{} balance", transaction.getFrom());
                        try {
                            ethService.syncAddressBalance(transaction.getFrom());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return deposits;
    }


    public synchronized int replayBlockInit(Long startBlockNumber,Long endBlockNumber) throws IOException {
        int count = 0;
        for(Long i=startBlockNumber;i <= endBlockNumber;i++){
            EthBlock block =  web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(i),true).send();

            block.getBlock().getTransactions().stream().forEach(transactionResult -> {
                EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
                Transaction transaction = transactionObject.get();
                if(StringUtils.isNotEmpty(transaction.getTo())
                        && accountService.isAddressExist(transaction.getTo())
                        && !transaction.getFrom().equalsIgnoreCase(getCoin().getIgnoreFromAddress())) {
                    Deposit deposit = new Deposit();
                    deposit.setTxid(transaction.getHash());
                    deposit.setBlockHeight(transaction.getBlockNumber().longValue());
                    deposit.setBlockHash(transaction.getBlockHash());
                    deposit.setAmount(Convert.fromWei(transaction.getValue().toString(), Convert.Unit.ETHER));
                    deposit.setAddress(transaction.getTo());
                    logger.info("received coin {} at height {}",transaction.getValue(),transaction.getBlockNumber());
                    depositEvent.onConfirmed(deposit);
                    //同步余额
                    try {
                        ethService.syncAddressBalance(deposit.getAddress());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //如果是地址簿里转出去的地址，需要同步余额
                if(StringUtils.isNotEmpty(transaction.getTo()) && accountService.isAddressExist(transaction.getFrom())) {
                    logger.info("sync address:{} balance",transaction.getFrom());
                    try {
                        ethService.syncAddressBalance(transaction.getFrom());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return count;
    }

    @Override
    public Long getNetworkBlockHeight() {
        try {
            EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
            return blockNumber.getBlockNumber().longValue();
        }catch (Exception e){
            e.printStackTrace();
            return 0L;
        }
    }
}
