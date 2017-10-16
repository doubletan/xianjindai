package com.xinhe.cashloan.entity;

import java.util.ArrayList;

/**
 * Created by tantan on 2017/7/13.
 */

public class CreditProduct {


    private ArrayList<CardListProduct> cardList;

    public ArrayList<CardListProduct> getCardList() {
        return cardList;
    }

    public void setCardList(ArrayList<CardListProduct> cardList) {
        this.cardList = cardList;
    }

    public static class CardListProduct {
        /**
         * cname : 交通银行
         * uid : 1
         * logo : /SysManage/UploadFile/cr/20161028013825171434954.png
         * corder : 1
         * clink : http://www.omsys.com.cn/jiaohangwl/index.php?aid=amlhb2hhbmd3bF81MzM3Xzk5X3llcw==
         * jianjie : 申请送好礼
         * tip1 : 急速审批
         * tip2 : 下卡快
         * tip3 : 额度高
         */

        private String cname;
        private String uid;
        private String logo;
        private String corder;
        private String clink;
        private String jianjie;
        private String tip1;
        private String tip2;
        private String tip3;

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
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

        public String getCorder() {
            return corder;
        }

        public void setCorder(String corder) {
            this.corder = corder;
        }

        public String getClink() {
            return clink;
        }

        public void setClink(String clink) {
            this.clink = clink;
        }

        public String getJianjie() {
            return jianjie;
        }

        public void setJianjie(String jianjie) {
            this.jianjie = jianjie;
        }

        public String getTip1() {
            return tip1;
        }

        public void setTip1(String tip1) {
            this.tip1 = tip1;
        }

        public String getTip2() {
            return tip2;
        }

        public void setTip2(String tip2) {
            this.tip2 = tip2;
        }

        public String getTip3() {
            return tip3;
        }

        public void setTip3(String tip3) {
            this.tip3 = tip3;
        }
    }
}
