package com.bizzan.bc.wallet.component;

import com.bizzan.bc.wallet.component.Watcher;
import com.bizzan.bc.wallet.entity.Contract;
import com.bizzan.bc.wallet.entity.Deposit;
import com.bizzan.bc.wallet.event.DepositEvent;
import com.bizzan.bc.wallet.service.AccountService;
import com.bizzan.bc.wallet.service.EtherscanApi;
import com.bizzan.bc.wallet.util.EthConvert;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Component
public class TokenWatcher extends Watcher{
    private Logger logger = LoggerFactory.getLogger(TokenWatcher.class);
    @Autowired
    private Web3j web3j;
    @Autowired
    private Contract contract;
    @Autowired
    private AccountService accountService;
    @Autowired(required = false)
    private EtherscanApi etherscanApi;

    @Autowired
    private DepositEvent depositEvent;

    public List<Deposit> replayBlock(Long startBlockNumber,Long endBlockNumber){
        List<Deposit> deposits = new ArrayList<>();
        for(Long blockHeight = startBlockNumber;blockHeight<=endBlockNumber;blockHeight++) {
            EthBlock block = null;
            try {
                logger.info("ethGetBlockByNumber {}", blockHeight);
                block = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockHeight), true).send();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<EthBlock.TransactionResult> transactionResults = block.getBlock().getTransactions();
            logger.info("replayBlock: Height({}) - Transactions count({})", blockHeight, transactionResults.size());
            for(EthBlock.TransactionResult transactionResult:transactionResults){
                EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
                Transaction transaction = transactionObject.get();
//                try {
//                	   合约执行结果判断，此处暂时注释掉，后面需要确认是否一定需要
//                    EthGetTransactionReceipt receipt =  web3j.ethGetTransactionReceipt(transaction.getHash()).send();
//                    if(receipt.getTransactionReceipt().get().getStatus().equalsIgnoreCase("0x1")){

//                		logger.info("Transaction Detail: Height({}) - {}", blockHeight, transaction.getHash());
                
                        String input = transaction.getInput();
                        String cAddress = transaction.getTo();
                        if (StringUtils.isNotEmpty(input) && input.length() >= 138 && contract.getAddress().equalsIgnoreCase(cAddress)) {
                        	logger.info("Transaction is contract: Height({}) - {}", blockHeight, contract.getAddress());
                            String data = input.substring(0, 9);
                            data = data + input.substring(17, input.length());
                            Function function = new Function("transfer", Arrays.asList(), Arrays.asList(new TypeReference<Address>() {
                            }, new TypeReference<Uint256>() {
                            }));

                            List<Type> params = FunctionReturnDecoder.decode(data, function.getOutputParameters());
                            // 充币地址
                            String toAddress = params.get(0).getValue().toString();
                            String amount = params.get(1).getValue().toString();
                            logger.info("Scan Address: " + toAddress + ", amount: " + amount);
                            if(accountService.isAddressExist(toAddress)) {
                            	logger.info("============> Find a deposit address: " + toAddress + ", amount: " + amount);
                            	//当eventTopic0参数不为空时检查event_log结果，防止低版本的token假充值
                            	if(StringUtils.isNotEmpty(contract.getEventTopic0()) && etherscanApi != null){
                                    boolean checkEvent = etherscanApi.checkEventLog(blockHeight,contract.getAddress(),contract.getEventTopic0(),transaction.getHash());
                                    if(!checkEvent) continue;
                                }
                            	logger.info("Transaction is Deposit: {} ", transaction.getHash());
                            	// 获取充值信息
                                if(StringUtils.isNotEmpty(amount)){
                                    Deposit deposit = new Deposit();
                                    deposit.setTxid(transaction.getHash());
                                    deposit.setBlockHash(transaction.getBlockHash());
                                    deposit.setAmount(EthConvert.fromWei(amount, contract.getUnit()));
                                    deposit.setAddress(toAddress);
                                    deposit.setTime(Calendar.getInstance().getTime());
                                    logger.info("receive {} {}",deposit.getAmount(),getCoin().getUnit());
                                    deposit.setBlockHeight(transaction.getBlockNumber().longValue());
                                    deposits.add(deposit);
                                }
                            }
                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        }
        return deposits;
    }

    /* 旧版：本地全节点模式
    public List<Deposit> replayBlock(Long startBlockNumber,Long endBlockNumber){
        List<Deposit> deposits = new ArrayList<>();
        for(Long blockHeight = startBlockNumber;blockHeight<=endBlockNumber;blockHeight++) {
            EthBlock block = null;
            try {
                logger.info("ethGetBlockByNumber {}", blockHeight);
                block = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockHeight), true).send();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<EthBlock.TransactionResult> transactionResults = block.getBlock().getTransactions();
            logger.info("transactionCount {}", transactionResults.size());
            for(EthBlock.TransactionResult transactionResult:transactionResults){
                EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
                Transaction transaction = transactionObject.get();
                try {
                	logger.info("transaction hash: {}", transaction.getHash());
                    EthGetTransactionReceipt receipt =  web3j.ethGetTransactionReceipt(transaction.getHash()).send();
                    logger.info("transaction hash result: {}", receipt.getTransactionReceipt().isPresent());
                    if(receipt.getTransactionReceipt().get().getStatus().equalsIgnoreCase("0x1")){
                        //当eventTopic0参数不为空时检查event_log结果，防止低版本的token假充值
                        if(StringUtils.isNotEmpty(contract.getEventTopic0()) && etherscanApi != null){
                            boolean checkEvent = etherscanApi.checkEventLog(blockHeight,contract.getAddress(),contract.getEventTopic0(),transaction.getHash());
                            if(!checkEvent) continue;
                        }
                        String input = transaction.getInput();
                        String cAddress = transaction.getTo();
                        if (StringUtils.isNotEmpty(input) && input.length() >= 138 && contract.getAddress().equalsIgnoreCase(cAddress)) {
                            String data = input.substring(0, 9);
                            data = data + input.substring(17, input.length());
                            Function function = new Function("transfer", Arrays.asList(), Arrays.asList(new TypeReference<Address>() {
                            }, new TypeReference<Uint256>() {
                            }));

                            List<Type> params = FunctionReturnDecoder.decode(data, function.getOutputParameters());
                            // 充币地址
                            String toAddress = params.get(0).getValue().toString();
                            String amount = params.get(1).getValue().toString();
                            if(accountService.isAddressExist(toAddress)) {
                                if(StringUtils.isNotEmpty(amount)){
                                    Deposit deposit = new Deposit();
                                    deposit.setTxid(transaction.getHash());
                                    deposit.setBlockHash(transaction.getBlockHash());
                                    deposit.setAmount(EthConvert.fromWei(amount, contract.getUnit()));
                                    deposit.setAddress(toAddress);
                                    deposit.setTime(Calendar.getInstance().getTime());
                                    logger.info("receive {} {}",deposit.getAmount(),getCoin().getUnit());
                                    deposit.setBlockHeight(transaction.getBlockNumber().longValue());
                                    deposits.add(deposit);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return deposits;
    }
     */
    public synchronized void replayBlockInit(Long startBlockNumber,Long endBlockNumber){
        for(long i = startBlockNumber;i<=endBlockNumber;i++) {
            EthBlock block = null;
            try {
                logger.info("ethGetBlockByNumber {}", i);
                block = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(i), true).send();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<EthBlock.TransactionResult> transactionResults = block.getBlock().getTransactions();
            logger.info("transactionCount {}", transactionResults.size());
            transactionResults.forEach(transactionResult -> {

                EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
                Transaction transaction = transactionObject.get();
                try {
                    EthGetTransactionReceipt receipt =  web3j.ethGetTransactionReceipt(transaction.getHash()).send();
                    if(receipt.getTransactionReceipt().get().getStatus().equalsIgnoreCase("0x1")){
                        String input = transaction.getInput();
                        String cAddress = transaction.getTo();
                        if (StringUtils.isNotEmpty(input) && input.length() >= 138 && contract.getAddress().equalsIgnoreCase(cAddress)) {
                            String data = input.substring(0, 9);
                            data = data + input.substring(17, input.length());
                            Function function = new Function("transfer", Arrays.asList(), Arrays.asList(new TypeReference<Address>() {
                            }, new TypeReference<Uint256>() {
                            }));

                            List<Type> params = FunctionReturnDecoder.decode(data, function.getOutputParameters());
                            // 充币地址
                            String toAddress = params.get(0).getValue().toString();
                            String amount = params.get(1).getValue().toString();
                            logger.info("################{}###################{}",toAddress,amount);
                            if(accountService.isAddressExist(toAddress)) {
                                if(StringUtils.isNotEmpty(amount)){
                                    Deposit deposit = new Deposit();
                                    deposit.setTxid(transaction.getHash());
                                    deposit.setBlockHash(transaction.getBlockHash());
                                    deposit.setAmount(EthConvert.fromWei(amount, contract.getUnit()));
                                    deposit.setAddress(toAddress);
                                    deposit.setTime(Calendar.getInstance().getTime());
                                    deposit.setBlockHeight(transaction.getBlockNumber().longValue());
                                    depositEvent.onConfirmed(deposit);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
    }

    @Override
    public Long getNetworkBlockHeight() {
        try {
            EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
            long networkBlockNumber = blockNumber.getBlockNumber().longValue();
            return networkBlockNumber;
        }
        catch (Exception e){
            e.printStackTrace();
            return 0L;
        }
    }
}
