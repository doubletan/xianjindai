package com.xinhe.cashloan.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by tantan on 2017/7/11.
 */

public class Product implements Serializable{


    private ArrayList<PrdListProduct> prdList;

    public ArrayList<PrdListProduct> getPrdList() {
        return prdList;
    }

    public void setPrdList(ArrayList<PrdListProduct> prdList) {
        this.prdList = prdList;
    }

    public static class PrdListProduct implements Comparable<Product.PrdListProduct>,Serializable{
        /**
         * name : 及贷
         * uid : 90
         * logo : /SysManage/UploadFile/wd/20170503131239467704933.png
         * orderpm : 11
         * link : http://m.haomoney.com/activity/reg/register.html?utm_source=wap_qiangfeng8
         * summary : 1000~10000门槛低、快速到账
         * lines : 1000-10000元
         * timeLimit : 1~12月
         * rate : 0.03%/日
         * speed : 最快5分钟到账
         * difficulty : 10%-30%
         * demand1 : 实名认证、基本信息、手机授权
         * demand2 : 大额需芝麻信用
         * tips1 : 支持部分还款、提前还款、有人行征信报告可提额
         * tips2 : 客服电话：400-6869586
         */

        private String name;
        private String uid;
        private String logo;
        private String orderpm;
        private String link;
        private String summary;
        private String lines;
        private String timeLimit;
        private String rate;
        private String speed;
        private String difficulty;
        private String demand1;
        private String demand2;
        private String tips1;
        private String tips2;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getOrderpm() {
            return orderpm;
        }

        public void setOrderpm(String orderpm) {
            this.orderpm = orderpm;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getLines() {
            return lines;
        }

        public void setLines(String lines) {
            this.lines = lines;
        }

        public String getTimeLimit() {
            return timeLimit;
        }

        public void setTimeLimit(String timeLimit) {
            this.timeLimit = timeLimit;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public String getDemand1() {
            return demand1;
        }

        public void setDemand1(String demand1) {
            this.demand1 = demand1;
        }

        public String getDemand2() {
            return demand2;
        }

        public void setDemand2(String demand2) {
            this.demand2 = demand2;
        }

        public String getTips1() {
            return tips1;
        }

        public void setTips1(String tips1) {
            this.tips1 = tips1;
        }

        public String getTips2() {
            return tips2;
        }

        public void setTips2(String tips2) {
            this.tips2 = tips2;
        }

        @Override
        public int compareTo(@NonNull PrdListProduct o) {
            return orderpm.compareTo(o.getOrderpm());
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "prdList=" + prdList +
                '}';
    }
}
