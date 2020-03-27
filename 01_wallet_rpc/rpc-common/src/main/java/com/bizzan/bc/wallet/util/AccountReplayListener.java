package com.bizzan.bc.wallet.util;

import com.bizzan.bc.wallet.entity.Account;

public interface AccountReplayListener {

    void replay(Account account);
}
