package com.aliyun.iot.ilop.demo.page.bean;

import com.aliyun.iot.ilop.demo.kaiguan.bean.ScenesBean;

import java.util.ArrayList;

public class AddSceneBean {
     private String groupId;
     private boolean enable;
     private String name;
     private String icon;
     private String iconColor;
     private ArrayList<ScenesBean> actions;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconColor() {
        return iconColor;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public ArrayList<ScenesBean> getActions() {
        return actions;
    }

    public void setActions(ArrayList<ScenesBean> actions) {
        this.actions = actions;
    }
}
