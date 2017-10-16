package com.xinhe.cashloan.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的BaseAdapter，本案例中所有Adapter都应该继承自该Adapter
 *
 * @param <T>
 *            数据源中List集合里的数据类型
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
	/**
	 * 上下文对象
	 */
	private Context context;
	/**
	 * 数据源
	 */
	private List<T> data;
	/**
	 * 将模板加载为布局对象的工具
	 */
	private LayoutInflater layoutInflater;

	/**
	 * 构造方法
	 *
	 * @param context
	 *            上下文对象，不允许为null
	 * @param data
	 *            数据源
	 */
	public BaseAdapter(Context context, List<T> data) {
		super();
		setContext(context);
		setData(data);
		setLayoutInflater();
	}

	/**
	 * 获取Context对象
	 *
	 * @return Context对象
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * 设置Context对象
	 *
	 * @param context
	 *            Context对象，不允许为null
	 */
	public void setContext(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("参数Context不允许为null！！！");
		}
		this.context = context;
	}

	/**
	 * 获取数据源
	 *
	 * @return 数据源
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * 设置数据源
	 *
	 * @param data
	 *            数据源
	 */
	public void setData(List<T> data) {
		if (data == null) {
			data = new ArrayList<T>();
		}
		this.data = data;
	}

	/**
	 * 获取LayoutInflater对象
	 *
	 * @return LayoutInflater对象
	 */
	public LayoutInflater getLayoutInflater() {
		return layoutInflater;
	}

	/**
	 * 设置LayoutInflater
	 */
	private void setLayoutInflater() {
		this.layoutInflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
