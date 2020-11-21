package com.aliyun.iot.ilop.page.mine.tripartite_platform.activity;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.page.mine.MineConstants;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter.TmallGenieAdapter;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.BindTaoBaoAccountBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.GetThirdpartyBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.TripartitePlatformListBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.bean.UnBindBean;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.handler.TmallGenieHandler;
import com.aliyun.iot.ilop.page.mine.tripartite_platform.interfaces.ITmallGenieActivity;
import com.aliyun.iot.link.ui.component.LinkToast;
import com.aliyun.iot.link.ui.component.LoadingCompact;
import com.aliyun.iot.link.ui.component.nav.UIBarItem;
import com.aliyun.iot.link.ui.component.nav.UINavigationBar;

/**
 * @author uk
 */
public class TmallGenieActivity extends MineBaseActivity implements ITmallGenieActivity {
    public static final int REQUEST_CODE = 0X101;
    private UINavigationBar navigationBar;
    private RecyclerView recyclerView;
    private TmallGenieHandler mHandler;
    private String channel;
    private TmallGenieAdapter mAdapter;
    private int headImgResId;
    private boolean isTMFlag = false;
    private boolean bindAccountFlag = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ilop_mine_tmall_genie_activity);
    }

    @Override
    protected void onDestroy() {
        if (null != mHandler) {
            mHandler.destroy();
        }
        super.onDestroy();
    }


    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        recyclerView = findViewById(R.id.recycler_view);
    }


    protected void initData() {
        String title = getIntent().getStringExtra(MineConstants.TITLE);
        navigationBar.setTitle(TextUtils.isEmpty(title) ? "" : title);
        navigationBar.setDisplayDividerEnable(false);
        channel = getIntent().getStringExtra(MineConstants.CHANNEL);
        config(channel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TmallGenieAdapter(this, channel, true);
        recyclerView.setAdapter(mAdapter);
    }


    protected void initEvent() {
        navigationBar.setNavigationBackAction(new UIBarItem.Action() {
            @Override
            public void invoke(View view) {
                onBackPressed();
            }
        });
    }


    protected void initHandler() {
        mHandler = new TmallGenieHandler(this);
        refreshList();
    }

    private void refreshList() {
        if (mHandler != null) {
            mHandler.requestDeviceList(channel, 1, 30);
            if (isTMFlag) {
                mHandler.queryAccount(MineConstants.ACCOUNT_TYPE);
            }
            showLoading();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == H5Activity.RESULT_CODE) {
            String authCode = data.getStringExtra("AuthCode");
            if (null != mHandler) {
                mHandler.bindAccount(authCode);
            }
        }
    }

    @Override
    public void setTitle() {

    }

    @Override
    public void showList(TripartitePlatformListBean bean) {
        bean.setImg(headImgResId);
        bean.setChannel(channel);
        mAdapter.setList(bean.getData(), bean);
    }

    @Override
    public void showEmptyList() {

    }

    @Override
    public void showLoading() {
        LoadingCompact.showLoading(this);
    }

    @Override
    public void showLoaded() {
        LoadingCompact.dismissLoading(this, true);
    }

    @Override
    public void showLoadError() {
        TripartitePlatformListBean bean = new TripartitePlatformListBean();
        bean.setImg(headImgResId);
        bean.setChannel(channel);
        mAdapter.setList(bean.getData(), bean);
        LinkToast.makeText(this, "component_network_exception").setGravity(Gravity.CENTER).show();
    }

    @Override
    public void bindAccountSuccess(BindTaoBaoAccountBean data) {
        bindAccountFlag = true;
        mAdapter.isBindAccountFlag(bindAccountFlag);
        LinkToast.makeText(this, "thirdparty_binded_account_success").setGravity(Gravity.CENTER).show();
    }

    @Override
    public void bindAccountFail() {
        bindAccountFlag = false;
        mAdapter.isBindAccountFlag(bindAccountFlag);
        LinkToast.makeText(this, "thirdparty_binded_account_failed").setGravity(Gravity.CENTER).show();
    }

    @Override
    public void unBindAccountSuccess(UnBindBean data) {
        bindAccountFlag = false;
        mAdapter.isBindAccountFlag(bindAccountFlag);
        LinkToast.makeText(this, "thirdparty_unbinded_account_success").setGravity(Gravity.CENTER).show();
    }

    @Override
    public void unBindAccountFail() {
        LinkToast.makeText(this, "thirdparty_binded_account_failed").setGravity(Gravity.CENTER).show();
    }

    @Override
    public void queryTAOBAOAccountSuccess(GetThirdpartyBean data) {
        bindAccountFlag = null != data && null != data.getLinkIndentityId();
        mAdapter.isBindAccountFlag(bindAccountFlag);
    }

    @Override
    public void queryTAOBAOAccountFail() {
        bindAccountFlag = false;
        mAdapter.isBindAccountFlag(bindAccountFlag);
    }

    public void config(String channel) {
        headImgResId = 0;
        isTMFlag = false;
        switch (channel) {
            case MineConstants.TM:
                headImgResId = R.drawable.tmallgenie;
                isTMFlag = true;
                break;
            case MineConstants.AA:
                headImgResId = R.drawable.amazonalexa;
                break;
            case MineConstants.GA:
                headImgResId = R.drawable.googleassistant;
                break;
            default:
                headImgResId = R.drawable.ifttt;
                break;
        }
    }

    public void unBindAccount() {
        String accountId = "";
        mHandler.unBindAccount(accountId, MineConstants.ACCOUNT_TYPE);
    }
}
