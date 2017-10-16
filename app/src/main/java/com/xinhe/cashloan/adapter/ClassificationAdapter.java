package com.xinhe.cashloan.adapter;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itheima.roundedimageview.RoundedImageView;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.entity.Classification;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.util.Constants;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassificationAdapter extends BaseAdapter<Classification.TypeListProduct> {

	private ArrayList<Classification.TypeListProduct> data;
	private Context context;

	public ClassificationAdapter(Context context, ArrayList<Classification.TypeListProduct> data) {
		super(context, data);
		if (data == null) {
			this.data = new ArrayList<Classification.TypeListProduct>();
		} else {
			this.data = data;
		}
		this.context=context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 获取当前第position条列表项需要显示的数据
		Classification.TypeListProduct cr = data.get(position);

		// 准备ViewHolder
		ViewHolder holder;

		// 判断convertView是否是重复使用的，如果不是，则convertView为null，需要从头加载布局等，否则，convertView是被重复使用的，则无须再次根据模板加载对象
		if (convertView == null) {
			// 加载模板得到View对象
			convertView = getLayoutInflater().inflate(R.layout.main_classification_item, null);
			// 创建新的ViewHolder
			holder = new ViewHolder();
			// 从模板对象中获取控件
			holder.t1 = (TextView) convertView.findViewById(R.id.tv1_creditcard_item);
			holder.t2 = (TextView) convertView.findViewById(R.id.tv4_creditcard_item);
			holder.i1 = (ImageView) convertView.findViewById(R.id.iv_creditcard_item);
			// 将TextView封装到convertView中
			convertView.setTag(holder);
		} else {
			// 从convertView中获取之前封装的数据
			holder = (ViewHolder) convertView.getTag();
		}

		// 设置显示数据
		holder.t1.setText(cr.getTips1());
		holder.t2.setText(cr.getTips2());
		int position1 = position % 6;
		if (0==position1){
			Glide.with(context).load(R.mipmap.classification1).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
		}else if (1==position1){
			Glide.with(context).load(R.mipmap.classification2).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
		}else if (2==position1){
			Glide.with(context).load(R.mipmap.classification3).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
		}else if (3==position1){
			Glide.with(context).load(R.mipmap.classification4).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
		}else if (4==position1){
			Glide.with(context).load(R.mipmap.classification5).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
		}else if (5==position1){
			Glide.with(context).load(R.mipmap.classification6).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.i1);
		}

		// 返回由数据和模板组装成的列表项对象
		return convertView;
	}

	/**
	 * View控件的持有者
	 */
	class ViewHolder {
		TextView t1;
		TextView t2;
		ImageView i1;
	}

}
