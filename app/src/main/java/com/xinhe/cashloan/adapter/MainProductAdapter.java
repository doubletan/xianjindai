package com.xinhe.cashloan.adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.itheima.roundedimageview.RoundedImageView;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.ExceptionUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by tantan on 2017/9/28.
 */

public class MainProductAdapter extends BaseQuickAdapter<Product.PrdListProduct,BaseViewHolder> {

    private int size;
    //借款人数
    int l = (int) (System.currentTimeMillis()/100000)-15000000;
    int[] ls;


    public MainProductAdapter(ArrayList<Product.PrdListProduct> data) {
        super(R.layout.main_product_item, data);
        size=data.size();
        ls=new int[data.size()+1];
        for (int i=0;i<data.size()+1;i++){
            ls[i]=new Random().nextInt(l)+1000;
        }
        Arrays.sort(ls);
    }

    @Override
    protected void convert(BaseViewHolder helper, Product.PrdListProduct item) {
        if (size!=mData.size()){
            size=mData.size();
            ls=new int[mData.size()+1];
            for (int i=0;i<mData.size()+1;i++){
                ls[i]=new Random().nextInt(l)+1000;
            }
            Arrays.sort(ls);
        }
        helper.setText(R.id.main_product_item_tv1,item.getName());
        helper.setText(R.id.main_product_item_tv3,item.getSummary());
        helper.setText(R.id.main_product_item_tv5,""+ls[ls.length-1-helper.getPosition()]);
        Glide.with(mContext).load(Constants.piURL+item.getLogo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into((RoundedImageView) helper.getView(R.id.main_product_item_iv));
    }
}
