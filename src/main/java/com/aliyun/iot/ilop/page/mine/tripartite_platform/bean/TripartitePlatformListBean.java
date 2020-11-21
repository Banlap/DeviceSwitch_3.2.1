package com.aliyun.iot.ilop.page.mine.tripartite_platform.bean;

import java.util.List;

public class TripartitePlatformListBean {
    private String channel;
    private int img;
    private int total;
    private int pageNo;
    private int pageSize;
    private List<DataBean> data;

    public int getTotal() {
        return total;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * label : haha
         * value : TpRSuZB7FOiRzo50jUtr0010dcbf10
         * icon : TpRSuZB7FOiRzo50jUtr0010dcbf10
         */

        private String label;
        private String value;
        private String icon;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
