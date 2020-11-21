package com.aliyun.iot.ilop.page.mine.tripartite_platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.page.mine.MineConstants;
import com.aliyun.iot.ilop.page.mine.view.TPListItem;
import com.aliyun.iot.link.ui.component.nav.UINavigationBar;

public class TripartitePlatformListActivity extends MineBaseActivity implements View.OnClickListener {

    private UINavigationBar navigationBar;
    private TPListItem iftttItem;
    private TPListItem googleItem;
    private TPListItem amazonItem;
    private TPListItem tmItem;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ilop_mine_tripartite_platfrom_list_activity);

    }


    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setDisplayDividerEnable(false);
        tmItem = findViewById(R.id.mine_tp_tmallgenine);
        amazonItem = findViewById(R.id.mine_tp_amazon);
        googleItem = findViewById(R.id.mine_tp_google);
        iftttItem = findViewById(R.id.mine_tp_ifttt);
    }

    protected void initData() {
        tmItem.setImageView(R.drawable.tmallgenie);
        amazonItem.setImageView(R.drawable.amazonalexa);
        googleItem.setImageView(R.drawable.googleassistant);
        iftttItem.setImageView(R.drawable.ifttt);
//        RegionManager.getStoredRegion().
//        if (mCountrySelected.equals("86")) {
//            tmItem.setVisibility(View.VISIBLE);
//        } else {
//            tmItem.setVisibility(View.GONE);
//        }
    }


    protected void initEvent() {
        navigationBar.setNavigationBackAction(view -> onBackPressed());
        tmItem.setOnClickListener(this);
        amazonItem.setOnClickListener(this);
        googleItem.setOnClickListener(this);
        iftttItem.setOnClickListener(this);
    }

    @Override
    protected void initHandler() {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Bundle bundle = new Bundle();
        Intent intent = new Intent(v.getContext(), TmallGenieActivity.class);
        if (id == R.id.mine_tp_tmallgenine) {
            bundle.putString(MineConstants.TITLE, "Tmall Genie");
            bundle.putString(MineConstants.CHANNEL, MineConstants.TM);
        } else if (id == R.id.mine_tp_amazon) {
            bundle.putString(MineConstants.TITLE, "Amazon alexa");
            bundle.putString(MineConstants.CHANNEL, MineConstants.AA);
        } else if (id == R.id.mine_tp_google) {
            bundle.putString(MineConstants.TITLE, "Google Assistant");
            bundle.putString(MineConstants.CHANNEL, MineConstants.GA);
        } else if (id == R.id.mine_tp_ifttt) {
            bundle.putString(MineConstants.TITLE, "IFTTT");
            bundle.putString(MineConstants.CHANNEL, MineConstants.IFTTT);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
