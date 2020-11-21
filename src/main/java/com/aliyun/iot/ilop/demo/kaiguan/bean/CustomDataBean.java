package com.aliyun.iot.ilop.demo.kaiguan.bean;

public class CustomDataBean {
    private MsgSendBean customData;
    private String msgTag;

    public MsgSendBean getCustomData() {
        return customData;
    }

    public void setCustomData(MsgSendBean customData) {
        this.customData = customData;
    }

    public String getMsgTag() {
        return msgTag;
    }

    public void setMsgTag(String msgTag) {
        this.msgTag = msgTag;
    }
}
