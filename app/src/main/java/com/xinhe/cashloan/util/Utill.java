package com.xinhe.cashloan.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utill {

	// 给ListView重新设置高度
	public static void resetHight(ListView listView) {
		if (listView == null)
			return;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);

	}

	// 给GridView重新设置高度，两列
	public static void resetGridViewHight(GridView listView) {
		if (listView == null)
			return;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i += 2) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += (listItem.getMeasuredHeight()+ listView.getVerticalSpacing());
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight - listView.getVerticalSpacing();
		listView.setLayoutParams(params);

	}

	// 给GridView重新设置高度,四列
	public static void resetGridViewHight1(GridView listView) {
		if (listView == null)
			return;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i += 4) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += (listItem.getMeasuredHeight() + listView.getVerticalSpacing());
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight - listView.getVerticalSpacing();
		listView.setLayoutParams(params);

	}

	// 给GridView重新设置高度,三列
	public static void resetGridViewHight2(GridView listView) {
		if (listView == null)
			return;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i += 3) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += (listItem.getMeasuredHeight() + listView.getVerticalSpacing());
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight - listView.getVerticalSpacing();
		listView.setLayoutParams(params);
	}

	// 获取状态栏高度
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}