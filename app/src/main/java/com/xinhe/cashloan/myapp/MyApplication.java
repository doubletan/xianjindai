package com.xinhe.cashloan.myapp;

import android.app.Activity;
import android.app.Application;

import com.xinhe.cashloan.entity.ImagerBean;
import com.xinhe.cashloan.entity.Product;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**哈哈哈
 * Created by tantan on 2017/7/7.
 */

public class MyApplication extends Application{
    /**
     * release=true 软件发布 false:开发中
     */
    public static boolean isRelease = true;
    //是否登录
    public static boolean isLogin;
    //    用户ID
    public static String userId;
    //    用户手机号
    public static String phone;
    //    推荐产品
    public static Product tenProduct;
    //    新产品
    public static Product newProduct;
    //    轮播图
    public static ImagerBean imagerBean;
    //分类
    public static Product bestProduct;

    private List<Activity> myActivity = new ArrayList<>();
    private static MyApplication instance;
    //渠道打包时，渠道
    private static String channel;


    @Override
    public void onCreate() {
        super.onCreate();
        //获取实例
        instance = this;

        //极光
        JPushInterface.init(this);

//        /**
//         * 渠道打包时使用
//         */
//        //渠道打包时，获取相应的渠道
//        channel = WalleChannelReader.getChannel(this.getApplicationContext());
//        Constants.channel1=channel;
//        //友盟
//        MobclickAgent.UMAnalyticsConfig um = new MobclickAgent.UMAnalyticsConfig(getApplicationContext(),"57870cbb67e58e332a000858",channel);
//        MobclickAgent. startWithConfigure(um);
    }

    public static MyApplication getApp(){
        return instance;
    }

    public void addToList(Activity activity){
        myActivity.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (Activity activity : myActivity){
            if (activity!=null){
                activity.finish();
            }
        }
        System.exit(0);
    }
}
