/*
 *   Copyright (c) 2020 Sinyuk
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.aliyun.iot.ilop.page.mine.tripartite_platform.adapter;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by david on 2018/4/9.
 *
 * @author david
 * @date 2018/04/09
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * @param data 绑定数据
     * @param maybeLatest 是否最后一条数据
     */
    public abstract void bindData(T data, boolean maybeLatest);
}
