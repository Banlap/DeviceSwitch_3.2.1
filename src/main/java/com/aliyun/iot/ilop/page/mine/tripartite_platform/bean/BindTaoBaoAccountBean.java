package com.aliyun.iot.ilop.page.mine.tripartite_platform.bean;

import java.util.List;

public class BindTaoBaoAccountBean {

    /**
     * accountId : 1075380881
     * accountType : TAOBAO
     * linkIdentityIds : ["5039opd8fe0d50de261fc9c0ba2f8e3263c10525"]
     */

    private String accountId;
    private String accountType;
    private List<String> linkIdentityIds;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<String> getLinkIdentityIds() {
        return linkIdentityIds;
    }

    public void setLinkIdentityIds(List<String> linkIdentityIds) {
        this.linkIdentityIds = linkIdentityIds;
    }
}
