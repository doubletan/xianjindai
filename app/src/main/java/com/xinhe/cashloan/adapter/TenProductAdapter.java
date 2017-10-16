package com.xinhe.cashloan.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.itheima.roundedimageview.RoundedImageView;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tantan on 2017/9/28.
 */

public class TenProductAdapter extends BaseQuickAdapter<Product.PrdListProduct,BaseViewHolder>{
    /**
     * 构造方法
     *
     * @param data
     */
    public TenProductAdapter(ArrayList<Product.PrdListProduct> data) {
        super(R.layout.ten_recyclerview_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Product.PrdListProduct item) {
        helper.setText(R.id.recycleview_item_tv,item.getName());
        Glide.with(mContext).load(Constants.piURL+item.getLogo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into((RoundedImageView) helper.getView(R.id.recycleview_item_iv));
    }
}
