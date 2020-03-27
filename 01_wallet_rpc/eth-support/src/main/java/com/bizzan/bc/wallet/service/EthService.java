package com.bizzan.bc.wallet.service;

import com.bizzan.bc.wallet.entity.Account;
import com.bizzan.bc.wallet.entity.Coin;
import com.bizzan.bc.wallet.entity.Contract;
import com.bizzan.bc.wallet.service.AccountService;
import com.bizzan.bc.wallet.util.EthConvert;
import com.bizzan.bc.wallet.util.MessageResult;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;


@Component
public class EthService {
    private Logger logger = LoggerFactory.getLogger(EthService.class);
    @Autowired
    private Coin coin;
    @Autowired
    private Web3j web3j;
    @Autowired
    private PaymentHandler paymentHandler;
    @Autowired
    private AccountService accountService;
    @Autowired
    private JsonRpcHttpClient jsonrpcClient;
    @Autowired(required = false)
    private Contract contract;

    public String createNewWallet(String account, String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException, CipherException {
    	logger.info("====>  Generate new wallet file for ETH.");
        String fileName = WalletUtils.generateNewWalletFile(password, new File(coin.getKeystorePath()), true);
        Credentials credentials = WalletUtils.loadCredentials(password, coin.getKeystorePath() + "/" + fileName);
        String address = credentials.getAddress();
        accountService.saveOne(account, fileName, address);
        return address;
    }


    /**
     * 同步余额
     *
     * @param address
     * @throws IOException
     */
    public void syncAddressBalance(String address) throws IOException {
        BigDecimal balance = getBalance(address);
        accountService.updateBalance(address, balance);
    }


    public MessageResult transferFromWithdrawWallet(String toAddress, BigDecimal amount, boolean sync, String withdrawId) {
        return transfer(coin.getKeystorePath() + "/" + coin.getWithdrawWallet(), coin.getWithdrawWalletPassword(), toAddress, amount, sync,withdrawId);
    }

    public MessageResult transfer(String walletFile, String password, String toAddress, BigDecimal amount,boolean sync,String withdrawId) {
        Credentials credentials;
        try {
            credentials = WalletUtils.loadCredentials(password, walletFile);
        } catch (IOException e) {
            e.printStackTrace();
            return new MessageResult(500, "钱包文件不存在");
        } catch (CipherException e) {
            e.printStackTrace();
            return new MessageResult(500, "解密失败，密码不正确");
        }
        if(sync) {
            return paymentHandler.transferEth(credentials, toAddress, amount);
        }
        else{
            paymentHandler.transferEthAsync(credentials, toAddress, amount,withdrawId);
            return new MessageResult(0,"提交成功");
        }
    }

    public BigDecimal getBalance(String address) throws IOException {
        EthGetBalance getBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        return Convert.fromWei(getBalance.getBalance().toString(), Convert.Unit.ETHER);
    }

    public BigInteger getGasPrice() throws IOException {
        EthGasPrice gasPrice = web3j.ethGasPrice().send();
        BigInteger baseGasPrice =  gasPrice.getGasPrice();
        return new BigDecimal(baseGasPrice).multiply(coin.getGasSpeedUp()).toBigInteger();
    }

    public MessageResult transferFromWallet(String address, BigDecimal amount, BigDecimal fee, BigDecimal minAmount) {
        logger.info("transferFromWallet 方法");
        List<Account> accounts = accountService.findByBalance(minAmount);
        if (accounts == null || accounts.size() == 0) {
            MessageResult messageResult = new MessageResult(500, "没有满足条件的转账账户(大于0.1)!");
            logger.info(messageResult.toString());
            return messageResult;
        }
        BigDecimal transferredAmount = BigDecimal.ZERO;
        for (Account account : accounts) {
            BigDecimal realAmount = account.getBalance().subtract(fee);
            if (realAmount.compareTo(amount.subtract(transferredAmount)) > 0) {
                realAmount = amount.subtract(transferredAmount);
            }
            MessageResult result = transfer(coin.getKeystorePath() + "/" + account.getWalletFile(), "", address, realAmount, true,"");
            if (result.getCode() == 0 && result.getData() != null) {
                logger.info("transfer address={},amount={},txid={}", account.getAddress(), realAmount, result.getData());
                transferredAmount = transferredAmount.add(realAmount);
                try {
                    syncAddressBalance(account.getAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (transferredAmount.compareTo(amount) >= 0) {
                break;
            }
        }
        MessageResult result = new MessageResult(0, "success");
        result.setData(transferredAmount);
        return result;
    }

    public MessageResult transferToken(String fromAddress, String toAddress, BigDecimal amount, boolean sync) {
        Account account = accountService.findByAddress(fromAddress);
        Credentials credentials;
        try {
            credentials = WalletUtils.loadCredentials("", coin.getKeystorePath() + "/" + account.getWalletFile());
        } catch (IOException e) {
            e.printStackTrace();
            return new MessageResult(500, "私钥文件不存在");
        } catch (CipherException e) {
            e.printStackTrace();
            return new MessageResult(500, "解密失败，密码不正确");
        }
        if(sync) {
            return paymentHandler.transferToken(credentials, toAddress, amount);
        }
        else{
            paymentHandler.transferTokenAsync(credentials, toAddress, amount,"");
            return new MessageResult(0,"提交成功");
        }
    }

    public MessageResult transferTokenFromWithdrawWallet(String toAddress, BigDecimal amount, boolean sync,String withdrawId) {
        Credentials credentials;
        try {
            //解锁提币钱包
            credentials = WalletUtils.loadCredentials(coin.getWithdrawWalletPassword(), coin.getKeystorePath() + "/" + coin.getWithdrawWallet());
        } catch (IOException e) {
            e.printStackTrace();
            return new MessageResult(500, "私钥文件不存在");
        } catch (CipherException e) {
            e.printStackTrace();
            return new MessageResult(500, "解密失败，密码不正确");
        }
        if(sync) {
            return paymentHandler.transferToken(credentials, toAddress, amount);
        }
        else{
            paymentHandler.transferTokenAsync(credentials, toAddress, amount, withdrawId);
            return new MessageResult(0,"提交成功");
        }
    }


    public BigDecimal getTokenBalance(String address) throws IOException {
        BigInteger balance = BigInteger.ZERO;
        Function fn = new Function("balanceOf", Arrays.asList(new org.web3j.abi.datatypes.Address(address)), Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(fn);
        Map<String, String> map = new HashMap<String, String>();
        map.put("to", contract.getAddress());
        map.put("data", data);
        try {
            String methodName = "eth_call";
            Object[] params = new Object[]{map, "latest"};
            String result = jsonrpcClient.invoke(methodName, params, Object.class).toString();
            if (StringUtils.isNotEmpty(result)) {
                if ("0x".equalsIgnoreCase(result) || result.length() == 2) {
                    result = "0x0";
                }
                balance = Numeric.decodeQuantity(result);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.info("查询接口ERROR");
        }
        return  EthConvert.fromWei(new BigDecimal(balance), contract.getUnit());
    }

    public BigDecimal getMinerFee(BigInteger gasLimit) throws IOException {
        BigDecimal fee = new BigDecimal(getGasPrice().multiply(gasLimit));
        return Convert.fromWei(fee, Convert.Unit.ETHER);
    }

    public Boolean isTransactionSuccess(String txid) throws IOException {
        EthTransaction transaction =  web3j.ethGetTransactionByHash(txid).send();
        try {
            if (transaction != null && transaction.getTransaction().get() != null) {
                Transaction tx = transaction.getTransaction().get();
                if (!tx.getBlockHash().equalsIgnoreCase("0x0000000000000000000000000000000000000000000000000000000000000000")) {
                    EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txid).send();
                    if (receipt != null && receipt.getTransactionReceipt().get().getStatus().equalsIgnoreCase("0x1")) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
