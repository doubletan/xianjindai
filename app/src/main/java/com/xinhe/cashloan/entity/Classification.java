package com.xinhe.cashloan.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tantan on 2017/9/30.
 */

public class Classification {

    private ArrayList<TypeListProduct> typeList;

    public ArrayList<TypeListProduct> getTypeList() {
        return typeList;
    }

    public void setTypeList(ArrayList<TypeListProduct> typeList) {
        this.typeList = typeList;
    }

    public static class TypeListProduct {
        /**
         * name : H5前12
         * f1 :
         * orderpm : 1
         * f2 :
         * lines : H5前12
         * tips1 :
         * tips2 :
         */

        private String name;
        private String f1;
        private String orderpm;
        private String f2;
        private String lines;
        private String tips1;
        private String tips2;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getF1() {
            return f1;
        }

        public void setF1(String f1) {
            this.f1 = f1;
        }

        public String getOrderpm() {
            return orderpm;
        }

        public void setOrderpm(String orderpm) {
            this.orderpm = orderpm;
        }

        public String getF2() {
            return f2;
        }

        public void setF2(String f2) {
            this.f2 = f2;
        }

        public String getLines() {
            return lines;
        }

        public void setLines(String lines) {
            this.lines = lines;
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
    }
}
