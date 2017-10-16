package com.xinhe.cashloan.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itheima.roundedimageview.RoundedImageView;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.util.Constants;

import java.util.ArrayList;

/**
 * Created by tantan on 2017/7/13.
 */

public class NewProductAdapter extends BaseAdapter<Product.PrdListProduct> {

       private final Context context;
    private ArrayList<Product.PrdListProduct> data;

    public NewProductAdapter(Context context, ArrayList<Product.PrdListProduct> data) {
        super(context, data);
        if (data == null) {
            this.data = new ArrayList<Product.PrdListProduct>();
        } else {
            this.data = data;
        }

        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前第position条列表项需要显示的数据
        Product.PrdListProduct cr = data.get(position);

        // 准备ViewHolder
        ViewHolder holder;

        // 判断convertView是否是重复使用的，如果不是，则convertView为null，需要从头加载布局等，否则，convertView是被重复使用的，则无须再次根据模板加载对象
        if (convertView == null) {
            // 加载模板得到View对象
            convertView = getLayoutInflater().inflate(R.layout.new_fragment_product_item, null);
            // 创建新的ViewHolder
            holder = new ViewHolder();
            // 从模板对象中获取控件
            holder.t1 = (TextView) convertView.findViewById(R.id.product_item_tv1);
            holder.t2 = (TextView) convertView.findViewById(R.id.product_item_tv21);
            holder.t3 = (TextView) convertView.findViewById(R.id.product_item_tv22);
            holder.t4 = (TextView) convertView.findViewById(R.id.product_item_tv3);
            holder.i1 = (RoundedImageView) convertView.findViewById(R.id.product_item_iv);
            // 将TextView封装到convertView中
            convertView.setTag(holder);
        } else {
            // 从convertView中获取之前封装的数据
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置显示数据
        holder.t1.setText(cr.getName());
        holder.t2.setText(cr.getTips1());
        holder.t3.setText(cr.getTips2());
        holder.t4.setText(cr.getSummary());
        Glide.with(context).load(Constants.piURL+cr.getLogo()).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
        // 返回由数据和模板组装成的列表项对象
        return convertView;
    }

    /**
     * View控件的持有者
     */
    class ViewHolder {
        TextView t1;
        TextView t2;
        TextView t3;
        TextView t4;
        RoundedImageView i1;
    }


}
