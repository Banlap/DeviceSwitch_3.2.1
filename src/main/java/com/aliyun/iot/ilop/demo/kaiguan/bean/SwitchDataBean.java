package com.aliyun.iot.ilop.demo.kaiguan.bean;

import java.io.Serializable;

public class SwitchDataBean implements Serializable {
    private String name;
    private String week;
    private boolean isEnable;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
