package com.aliyun.iot.ilop.demo.kaiguan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.kaiguan.activity.TroubleDetailsActivity;
import com.aliyun.iot.ilop.demo.kaiguan.bean.SwitchDataBean;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;


public class TroubleFragment extends Fragment implements View.OnClickListener {
    private ImageView saom_iamge;
    private RecyclerView rv_eventlist;
    private SmartRefreshLayout srl_homelist;
    private CanRVAdapter adapter;
    private TextView talto_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_trouble, container, false);
         initView(view);
        return view;
    }

    private void initView(View view) {
        talto_text=view.findViewById(R.id.talto_text);
        saom_iamge=view.findViewById(R.id.saom_iamge);
        saom_iamge.setVisibility(View.VISIBLE);
        saom_iamge.setOnClickListener(this);
        rv_eventlist=view.findViewById(R.id.rv_eventlist);
        srl_homelist=view.findViewById(R.id.srl_eventlist);
        rv_eventlist.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        talto_text.setText("故障");
        srl_homelist.setRefreshHeader(new ClassicsHeader(getActivity()));
        srl_homelist.setRefreshFooter(new ClassicsFooter(getActivity()));

        srl_homelist.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
               /* page++;
                initData();*/
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
               /* page = 1;
                initData();*/
            }
        });
        //WaittingDialog.initWaittingDialog(getActivity(), "正在加载中...");

        adapter = new CanRVAdapter<SwitchDataBean>(rv_eventlist, R.layout.item_trouble_list) {
            @Override
            protected void setView(CanHolderHelper helper, int position, SwitchDataBean bean) {
                helper.setText(R.id.name_text,bean.getName());
                LinearLayout trouble_layout=helper.getView(R.id.trouble_layout);
                trouble_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(), TroubleDetailsActivity.class);
                        startActivity(intent);
                    }
                });




            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {
            }
        };
        initData();
        rv_eventlist.setAdapter(adapter);


    }

    private void initData() {
       /* List<SwitchDataBean> list=new ArrayList<>();
        SwitchDataBean switchDataBean=new SwitchDataBean();
        switchDataBean.setName("飞利浦智能开关前顶灯80W");
        list.add(switchDataBean);
        SwitchDataBean switchDataBean1=new SwitchDataBean();
        switchDataBean1.setName("飞利浦智能开关前顶灯80W");
        list.add(switchDataBean1);
        SwitchDataBean switchDataBean2=new SwitchDataBean();
        switchDataBean2.setName("飞利浦智能开关前顶灯80W");
        list.add(switchDataBean2);
        SwitchDataBean switchDataBean3=new SwitchDataBean();
        switchDataBean3.setName("飞利浦智能开关前顶灯80W");
        list.add(switchDataBean3);

        adapter.setList(list);
        adapter.notifyDataSetChanged();
        srl_homelist.finishRefresh();
        srl_homelist.finishLoadMore();*/



    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saom_iamge:
                Router.getInstance().toUrl(getContext(), "page/scan");
                break;
        }
    }
}