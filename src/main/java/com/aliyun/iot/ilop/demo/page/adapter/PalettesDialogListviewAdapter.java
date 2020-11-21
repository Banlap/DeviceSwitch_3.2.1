package com.aliyun.iot.ilop.demo.page.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Type;

import com.aliyun.iot.demo.R;
import com.aliyun.iot.ilop.demo.page.bean.PalettesDialogBean;
import com.aliyun.iot.ilop.demo.utils.ColorTools;
import com.aliyun.iot.ilop.demo.utils.JsonTools;
import com.aliyun.iot.ilop.demo.view.CircleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class PalettesDialogListviewAdapter extends BaseAdapter {
    private Context context;
    private List<PalettesDialogBean> arr = new ArrayList<>();
    private String color = "";

    public PalettesDialogListviewAdapter(Context context) {
        this.context = context;
        arr = ColorTools.getColorData(context);
    }


    public List<PalettesDialogBean> getArr() {
        return arr;
    }

    public void setSelectPos(String color) {
        this.color = color;
        Log.d("PalettesDialogListviewA", color);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_palettes_dialog, null);
        TextView title = convertView.findViewById(R.id.item_palettes_title);
        CircleView circleView = convertView.findViewById(R.id.item_palettes_circleview);
        LinearLayout linearLayout = convertView.findViewById(R.id.item_palettes_ll);
        title.setText(arr.get(position).getTitle());
        circleView.setColor(arr.get(position).getColor());
        if (arr.get(position).getColor().equals(this.color)) {
            linearLayout.setBackgroundResource(R.drawable.shape_lamps_hsvdialog_select_bg);
        } else {
            linearLayout.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }


}
