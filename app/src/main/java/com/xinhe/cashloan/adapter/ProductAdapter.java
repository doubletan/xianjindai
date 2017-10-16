package com.xinhe.cashloan.adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.itheima.roundedimageview.RoundedImageView;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.util.Constants;

import java.util.ArrayList;

/**
 * Created by tantan on 2017/7/11.
 */

public class ProductAdapter extends BaseQuickAdapter<Product.PrdListProduct,BaseViewHolder> {


    public ProductAdapter(ArrayList<Product.PrdListProduct> products) {
        super(R.layout.main_fragment_product_item, products);
    }


    @Override
    protected void convert(BaseViewHolder helper, Product.PrdListProduct item) {
        helper.setText(R.id.product_item_tv1,item.getName());
        helper.setText(R.id.product_item_tv2,"额度"+item.getLines()+"，期限"+item.getTimeLimit());
        helper.setText(R.id.product_item_tv3,"利率"+item.getRate());
        Glide.with(mContext).load(Constants.piURL+item.getLogo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into((RoundedImageView) helper.getView(R.id.product_item_iv));
    }

}
