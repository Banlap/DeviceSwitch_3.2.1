package com.aliyun.iot.aep.oa.page.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.ui.model.CountrySort;
import com.alibaba.sdk.android.openaccount.util.ResourceUtils;
import com.aliyun.iot.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by feijie.xfj on 18/4/11.
 */
public class CountryCodeAdapter extends BaseAdapter {
    private List<CountrySort> mList;
    private Context mContext;

    public CountryCodeAdapter(Context mContext, List<CountrySort> list) {
        this.mContext = mContext;
        if (list == null) {
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
    }

    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        CountrySort mContent = mList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.ali_sdk_openaccount_mobile_country_item2, null);
            viewHolder.country_sortName = convertView.findViewById(ResourceUtils.getRId(this.mContext, "country_catalog"));
            viewHolder.country_sortName_text = convertView.findViewById(ResourceUtils.getRId(this.mContext, "country_catalog_text"));
            viewHolder.country_name = convertView.findViewById(ResourceUtils.getRId(this.mContext, "country_name"));
            viewHolder.country_number = convertView.findViewById(ResourceUtils.getRId(this.mContext, "country_code"));
            viewHolder.country_code_hint = convertView.findViewById(ResourceUtils.getRId(this.mContext, "country_code_hint"));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        int section = this.getSectionForPosition(position);
        if (position == this.getPositionForSection(section)) {
            viewHolder.country_sortName.setVisibility(View.VISIBLE);
            viewHolder.country_code_hint.setVisibility(View.INVISIBLE);
            if ("*".equals(mContent.sortLetters)) {
                viewHolder.country_sortName_text.setText(R.string.account_hot_country);
            } else {
                viewHolder.country_sortName_text.setText(mContent.sortLetters);
            }

        } else {
            viewHolder.country_code_hint.setVisibility(View.VISIBLE);
            viewHolder.country_sortName.setVisibility(View.GONE);
        }

        viewHolder.country_name.setText(this.mList.get(position).displayName);
        viewHolder.country_number.setText(this.mList.get(position).code);


        return convertView;
    }

    private int getSectionForPosition(int position) {
        return this.mList.get(position).sortLetters.charAt(0);
    }

    public int getPositionForSection(int section) {
        if (section != 42) {
            for (int i = 0; i < this.getCount(); ++i) {
                String sortStr = this.mList.get(i).sortLetters;
                char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }

            return -1;
        } else {
            return 0;
        }
    }

    public static class ViewHolder {
        LinearLayout country_sortName;
        TextView country_sortName_text;
        TextView country_name;
        TextView country_number;
        View country_code_hint;
    }
}
