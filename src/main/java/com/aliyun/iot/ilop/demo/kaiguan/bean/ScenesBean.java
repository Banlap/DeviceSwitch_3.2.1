package com.aliyun.iot.ilop.demo.kaiguan.bean;

import java.io.Serializable;

public class ScenesBean implements Serializable {
    private String uri;
    private SceneParamsBean params;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public SceneParamsBean getParams() {
        return params;
    }

    public void setParams(SceneParamsBean params) {
        this.params = params;
    }
}
