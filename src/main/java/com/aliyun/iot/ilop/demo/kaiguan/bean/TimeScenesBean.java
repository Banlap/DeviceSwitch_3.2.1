package com.aliyun.iot.ilop.demo.kaiguan.bean;

public class TimeScenesBean {
    private String cron;
    private String cronType;
    private String timezoneID;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getCronType() {
        return cronType;
    }

    public void setCronType(String cronType) {
        this.cronType = cronType;
    }

    public String getTimezoneID() {
        return timezoneID;
    }

    public void setTimezoneID(String timezoneID) {
        this.timezoneID = timezoneID;
    }
}
