package com.xinhe.cashloan.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by apple on 2017/4/11.
 */

public class ImagerBean implements Serializable {

    private ArrayList<DaohangProduct> Daohang;

    public ArrayList<DaohangProduct> getDaohang() {
        return Daohang;
    }

    public void setDaohang(ArrayList<DaohangProduct> Daohang) {
        this.Daohang = Daohang;
    }

    public static class DaohangProduct implements Comparable<DaohangProduct>{
        /**
         * link : https://www.yangqianguan.com/flexible/index?act=EnWKU8
         * advpath : /SysManage/UploadFile/Adv/20170412110528778121151.png
         * orderpm : 2
         */

        private String link;
        private String advpath;
        private String orderpm;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getAdvpath() {
            return advpath;
        }

        public void setAdvpath(String advpath) {
            this.advpath = advpath;
        }

        public String getOrderpm() {
            return orderpm;
        }

        public void setOrderpm(String orderpm) {
            this.orderpm = orderpm;
        }

        @Override
        public int compareTo(@NonNull DaohangProduct o) {
            return orderpm.compareTo(o.getOrderpm());
        }
    }

    @Override
    public String toString() {
        return "ImagerBean{" +
                "Daohang=" + Daohang +
                '}';
    }
}
