package com.samples.rx.$3advance.domain;

import com.samples.rx.$1basics.domain.Account;

import java.util.Map;

public class SearchResult extends DataModel {

    private Account account;

    public SearchResult(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "account=" + account +
                '}';
    }
}
