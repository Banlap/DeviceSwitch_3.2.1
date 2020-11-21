package com.aliyun.iot.ilop.demo.page.ota.interfaces;

import java.util.List;

import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceSimpleInfo;

/**
 * Created by david on 2018/4/10.
 *
 * @author david
 * @date 2018/04/10
 */
public interface IOTAListActivity {
    /**
     * 设置页面标题
     */
    void setTitle();

    /**
     * 显示可升级设备列表
     * @param infoList
     */
    void showList(List<OTADeviceSimpleInfo> infoList);

    /**
     * 显示空列表（无可升级设备）
     */
    void showEmptyList();

    /**
     * 显示加载中
     */
    void showLoading();

    /**
     * 显示加载完成
     */
    void showLoaded();

    /**
     * 显示加载错误
     */
    void showLoadError();
}
