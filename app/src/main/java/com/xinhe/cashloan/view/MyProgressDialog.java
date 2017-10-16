package com.xinhe.cashloan.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xinhe.cashloan.R;

/**
 * Created by tantan on 2017/7/12.
 */

public class MyProgressDialog extends ProgressDialog{
    private Context mContext;
    private String mLoadingTip;
    private TextView mLoadingTv;
    private ProgressBar mProgressBar;

    public MyProgressDialog(Context context, String content, int theme) {
        super(context,theme);
        this.mContext = context;
        this.mLoadingTip = content;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
//        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(mContext, mResid);
//        // 添加匀速转动动画
//        LinearInterpolator lir = new LinearInterpolator();
//        refreshingAnimation.setInterpolator(lir);
//        mProgressDialog.startAnimation(refreshingAnimation);
        mLoadingTv.setText(mLoadingTip);

    }

    public void setContent(String str) {
        mLoadingTv.setText(str);
    }

    private void initView() {
        setContentView(R.layout.my_circle_progressdialog);
        mLoadingTv = (TextView) findViewById(R.id.my_circle_progress_tv);
        mProgressBar = (ProgressBar) findViewById(R.id.my_circle_progress_pb);
    }
}
