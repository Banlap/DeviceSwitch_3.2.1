package com.aliyun.iot.ilop.demo.page.ota.activity;

import java.util.List;

import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.framework.AActivity;
import com.aliyun.iot.ilop.demo.page.ota.OTAConstants;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.ota.adapter.MineOTAListAdapter;
import com.aliyun.iot.ilop.demo.page.ota.adapter.MineOTAListAdapter.OnItemClickListener;
import com.aliyun.iot.ilop.demo.page.ota.bean.OTADeviceSimpleInfo;
import com.aliyun.iot.ilop.demo.page.ota.handler.OTAListActivityHandler;
import com.aliyun.iot.ilop.demo.page.ota.interfaces.IOTAListActivity;
import com.aliyun.iot.ilop.demo.page.ota.view.SimpleTopbar;
import com.aliyun.iot.ilop.demo.page.ota.view.SimpleTopbar.onBackClickListener;
import com.aliyun.iot.link.ui.component.simpleLoadview.SimpleLoadingDialog;

/**
 * Created by david on 2018/4/9.
 *
 * @author david
 * @date 2018/04/09
 */
public class OTAListActivity extends AActivity implements IOTAListActivity,
    OnItemClickListener, onBackClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyLinearlayout;
    private SimpleTopbar mTopbar;
    private SwipeRefreshLayout mRefreshLayout;
    private SimpleLoadingDialog mLoadingDialog;

    private boolean maybePullRefresh = false;

    private MineOTAListAdapter mAdapter;
    private OTAListActivityHandler mHandler;

    /* =====================lifecycle @start =====================*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ilop_ota_list_activity);

        initView();
        initEvent();
        initData();

        initHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null != mHandler) {
            mHandler.requestOTAList();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mLoadingDialog) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }

        if (null != mHandler) {
            mHandler.destroy();
        }
        super.onDestroy();
    }

    /* =====================lifecycle @end =====================*/

    protected void initView() {
        mTopbar = findViewById(R.id.mine_topbar);
        mRecyclerView = findViewById(R.id.mine_setting_ota_list_recyclerview);
        mEmptyLinearlayout = findViewById(R.id.mine_setting_ota_empty_linearlayout);
        mRefreshLayout = findViewById(R.id.mine_setting_ota_list_refresh_refreshlayout);

        mRefreshLayout.setColorSchemeResources(R.color.mine_color_1FC88B);
        mRefreshLayout.setNestedScrollingEnabled(false);

        mLoadingDialog = new SimpleLoadingDialog(this);
        mLoadingDialog.setLoadingViewStyle(R.style.OTALoadingStyle);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MineOTAListAdapter(this);
    }

    protected void initEvent() {
        if (null != mAdapter) {
            mAdapter.setOnItemClickListener(this);
        }

        if (null != mTopbar) {
            mTopbar.setOnBackClickListener(this);
        }

        if (null != mRefreshLayout) {
            mRefreshLayout.setOnRefreshListener(this);
        }
    }

    protected void initData() {
        mRecyclerView.setAdapter(mAdapter);

        setTitle();
    }

    protected void initHandler() {
        mHandler = new OTAListActivityHandler(this);
    }

    /* =====================implement IOTAActivity @start =====================*/

    @Override
    public void setTitle() {
        if (isFinishing()) {
            return;
        }

        if (null != mTopbar) {
            mTopbar.setTitle(getString(R.string.ota));
        }
    }

    @Override
    public void showList(List<OTADeviceSimpleInfo> infoList) {
        if (isFinishing()) {
            return;
        }

        if (null != infoList) {
            mAdapter.setData(infoList);
        }

        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyLinearlayout.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyList() {
        if (isFinishing()) {
            return;
        }

        mRecyclerView.setVisibility(View.GONE);
        mEmptyLinearlayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        if (isFinishing()) {
            return;
        }

        if (maybePullRefresh && null != mRefreshLayout) {
            mRefreshLayout.setRefreshing(true);
            return;
        }

        if (null != mLoadingDialog && !mLoadingDialog.isShowing()) {
            mLoadingDialog.showLoading(getString(R.string.ota_loading));
        }
    }

    @Override
    public void showLoaded() {
        if (isFinishing()) {
            return;
        }

        if (maybePullRefresh && null != mRefreshLayout) {
            mRefreshLayout.setRefreshing(false);
            maybePullRefresh = false;
        }

        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void showLoadError() {
        if (isFinishing()) {
            return;
        }

        Toast.makeText(this, getString(R.string.ota_network_error), Toast.LENGTH_SHORT).show();
    }

    /* =====================implement IOTAActivity @end =====================*/

    /* =====================OnItemClickListener @start =====================*/

    @Override
    public void onItemClick(OTADeviceSimpleInfo info, int position) {
        if (isFinishing()) {
            return;
        }

        if (null == info) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(OTAConstants.OTA_KEY_DEVICESIMPLE_INFO, info);
        Router.getInstance().toUrl(this, OTAConstants.MINE_URL_OTA, bundle);
    }

    /* =====================OnItemClickListener @end =====================*/

    /* =====================onBackClickListener @start =====================*/

    @Override
    public void onBackClick() {
        if (isFinishing()) {
            return;
        }

        this.finish();
    }

    /* =====================onBackClickListener @end =====================*/

    /* =====================OnRefreshListener @start =====================*/

    @Override
    public void onRefresh() {
        if (isFinishing()) {
            return;
        }

        if (null != mHandler) {
            maybePullRefresh = true;
            mHandler.requestOTAList();
        }
    }

    /* =====================OnRefreshListener @end =====================*/
}
