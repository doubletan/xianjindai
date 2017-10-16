package com.xinhe.cashloan.util;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xinhe.cashloan.R;

public class PopupWindowUtil {
	private static TextView tvPopupFirstButton;
	private static TextView tvPopupSecondButton;
	private static TextView tvPopupCancel;
	private static PopupWindow popupWindow;

	public static void showPopupWindow(Context context, View rootview, String firstBtnStr, String secondBtnStr,
			String cancelBtnStr, final onPupupWindowOnClickListener listener) {
		View contentView = LayoutInflater.from(context).inflate(R.layout.popupwindow_menu, null);
		tvPopupFirstButton = (TextView) contentView.findViewById(R.id.tv_first_button);
		tvPopupSecondButton = (TextView) contentView.findViewById(R.id.tv_second_button);
		tvPopupCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
		View divLine1 = contentView.findViewById(R.id.div_line1);
		View divLine2 = contentView.findViewById(R.id.div_line2);
		if (!TextUtils.isEmpty(firstBtnStr)) {
			tvPopupFirstButton.setText(firstBtnStr);

			tvPopupFirstButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onFirstButtonClick();
					if (popupWindow.isShowing()) {
						popupWindow.dismiss();

					}
				}
			});
		} else {
			tvPopupFirstButton.setVisibility(View.GONE);
			divLine1.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(secondBtnStr)) {
			tvPopupSecondButton.setText(secondBtnStr);
			// 第二个按钮点
			tvPopupSecondButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onSecondButtonClick();
					if (popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
				}
			});
		} else {
			tvPopupSecondButton.setVisibility(View.GONE);
			divLine2.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(cancelBtnStr)) {
			// 取消按钮点击
			tvPopupCancel.setText(cancelBtnStr);
			tvPopupCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					listener.onCancleButtonClick();
					if (popupWindow.isShowing())
						popupWindow.dismiss();
				}
			});
		} else {
			tvPopupCancel.setVisibility(View.GONE);
		}

		popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.pop_animation);
		popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);

	}

	public interface onPupupWindowOnClickListener {
		public void onFirstButtonClick();

		public void onSecondButtonClick();

		public void onCancleButtonClick();
	}
}
