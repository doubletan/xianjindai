package com.xinhe.cashloan.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.biz.GetClassificationProduct;
import com.xinhe.cashloan.biz.GetImageBean;
import com.xinhe.cashloan.biz.GetNewProduct;
import com.xinhe.cashloan.biz.GetTenProduct;
import com.xinhe.cashloan.util.SPUtils;

import java.lang.ref.WeakReference;


public class LauncherActivity extends AppCompatActivity {


    private SwitchHandler mHandler= new SwitchHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        //获取数据
        getData();
        setView();
        //全屏展示
        hideActionBar();
        setFullScreen();
    }


    /**
     * hide action bar
     */
    private void hideActionBar() {
        // Hide UI
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


    /**
     * set the activity display in full screen
     */
    private void setFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setView() {
        boolean isFirst = SPUtils.contains(this, "isFirst");
        if (isFirst){
            mHandler.sendEmptyMessageDelayed(2,2000);
        }else {
            mHandler.sendEmptyMessageDelayed(1,2000);
        }
    }

    private class SwitchHandler extends Handler {
        private WeakReference<LauncherActivity> mWeakReference;

        SwitchHandler(LauncherActivity activity) {
            mWeakReference = new WeakReference<LauncherActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LauncherActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what){
                    case 1:
                        Intent intent=new Intent(LauncherActivity.this,GuideActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent1=new Intent(LauncherActivity.this,MainActivity.class);
                        startActivity(intent1);
                        break;
                }
                finish();
            }

        }
    }

    private void getData() {
        //执行相关操作
        new GetNewProduct(this).execute();
        new GetImageBean(this).execute();
        new GetTenProduct(this).execute();
        new GetClassificationProduct(this).execute();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public void onBackPressed() {

    }
}
