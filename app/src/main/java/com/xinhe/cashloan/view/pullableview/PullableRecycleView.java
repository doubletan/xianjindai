package com.xinhe.cashloan.view.pullableview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by tantan on 2017/9/27.
 */

public class PullableRecycleView extends RecyclerView implements Pullable{
    public PullableRecycleView(Context context) {
        super(context);
    }

    public PullableRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        if (getChildCount() == 0) {
            // 没有item的时候也可以下拉刷新
            return true;
        } else if (getChildAt(0).getTop() >= 0) {
            if (getLayoutManager() instanceof LinearLayoutManager) {
                int firstVisibleItem = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                if (firstVisibleItem == 0) {
                    return true;
                }
            } else if (getLayoutManager() instanceof GridLayoutManager) {
                int firstVisibleItem = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                if (firstVisibleItem == 0) {
                    return true;
                }
            }
        } else
            return false;
        return false;
    }

    @Override
    public boolean canPullUp() {
        //		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
//			return true;
//		else
        return false;
    }
}
