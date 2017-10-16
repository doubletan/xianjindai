package com.xinhe.cashloan.view.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class PullableScrollView1 extends ScrollView implements Pullable {

	// �������뼰����
	private float xDistance, yDistance, xLast, yLast;

	public PullableScrollView1(Context context) {
		super(context);
	}

	public PullableScrollView1(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableScrollView1(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown() {
		if (getScrollY() == 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean canPullUp() {
//		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
//			return true;
//		else
			return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}

}
