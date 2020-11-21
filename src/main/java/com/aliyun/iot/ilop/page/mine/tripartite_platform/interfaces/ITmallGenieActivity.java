package com.aliyun.iot.ilop.page.mine.tripartite_platform.interfaces;

import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.BindTaoBaoAccountBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.GetThirdpartyBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.TripartitePlatformListBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.UnBindBean;

import java.util.List;

public interface ITmallGenieActivity {
    /**
     * 设置页面标题
     */
    void setTitle();

    /**
     * 显示可升级设备列表
     *
     * @param
     */
    void showList(TripartitePlatformListBean bean);

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

    void bindAccountSuccess(BindTaoBaoAccountBean data);

    void bindAccountFail();

    void unBindAccountSuccess(UnBindBean data);
    void unBindAccountFail();

    void queryTAOBAOAccountSuccess(GetThirdpartyBean data);

    void queryTAOBAOAccountFail();
}
