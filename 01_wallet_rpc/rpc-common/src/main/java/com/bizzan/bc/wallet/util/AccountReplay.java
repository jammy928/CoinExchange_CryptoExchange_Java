package com.bizzan.bc.wallet.util;

import com.bizzan.bc.wallet.entity.Account;
import com.bizzan.bc.wallet.service.AccountService;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
public class AccountReplay {
    private Logger logger = LoggerFactory.getLogger(AccountReplay.class);
    private AccountService accountService;
    private int pageSize = 100;

    public AccountReplay(AccountService service,int pageSize){
        this.accountService = service;
        this.pageSize = pageSize;
    }

    public void run(final AccountReplayListener listener){
        long count = accountService.count();
        long totalPage = count/this.pageSize;
        if(count%pageSize != 0){
            totalPage += 1;
        }
        logger.info("start replay account,total={},page={},pageSize={}",count,totalPage,pageSize);
        for(int page = 0;page<=totalPage;page++){
            List<Account> accounts = accountService.find(page,pageSize);
            accounts.forEach(account->{
                listener.replay(account);
            });
        }
    }
}
