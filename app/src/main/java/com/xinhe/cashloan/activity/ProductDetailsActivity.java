package com.xinhe.cashloan.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.biz.BrowsingHistory;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.DeviceUtil;
import com.xinhe.cashloan.util.ExceptionUtil;
import com.xinhe.cashloan.util.Utill;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductDetailsActivity extends Activity implements OnClickListener {

    @Bind(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @Bind(R.id.my_toolbar)
    Toolbar myToolbar;
    @Bind(R.id.iv_product_details)
    ImageView ivProductDetails;
    @Bind(R.id.tv_product_details_strategy)
    TextView tvProductDetailsStrategy;
    @Bind(R.id.product_details_limit_tv)
    TextView productDetailsLimitTv;
    @Bind(R.id.product_details_deadline_tv)
    TextView productDetailsDeadlineTv;
    @Bind(R.id.product_details_cost_tv)
    TextView productDetailsCostTv;
    @Bind(R.id.product_details_speed_tv)
    TextView productDetailsSpeedTv;
    @Bind(R.id.product_details_difficulty_tv)
    TextView productDetailsDifficultyTv;
    @Bind(R.id.tv_product_details_request)
    TextView tvProductDetailsRequest;
    @Bind(R.id.product_details_demand1_tv)
    TextView productDetailsDemand1Tv;
    @Bind(R.id.product_details_demand2_tv)
    TextView productDetailsDemand2Tv;
    @Bind(R.id.product_details_tips1_tv)
    TextView productDetailsTips1Tv;
    @Bind(R.id.product_details_tips2_tv)
    TextView productDetailsTips2Tv;
    @Bind(R.id.btn_product_details_apply)
    Button btnProductDetailsApply;
    private String name;
    private String logoUrl;
    private String url;
    private Product.PrdListProduct prdListProduct;
    private String proid;
    private JSONObject json;
    private String limit;
    private String deadline;
    private String cost;
    private String speed;
    private String difficulty;
    private String demand1;
    private String demand2;
    private String tips1;
    private String tips2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 4.4版本以上设置 全屏显示，状态栏在界面上方
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        // 设置顶部控件不占据状态栏
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            LinearLayout barLl = (LinearLayout) findViewById(R.id.rl_product_details_top);
            barLl.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) barLl.getLayoutParams();
            ll.height = Utill.getStatusBarHeight(this);
            ll.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            barLl.setLayoutParams(ll);
        }
        initActionBar();
        // 获取数据
        getData();
        // 设置控件
        setViews();
        // 给控件设置监听
        setListeners();
    }


    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setListeners() {
        btnProductDetailsApply.setOnClickListener(this);
    }

    private void setViews() {
        myToolbarTv.setText(name);
        tvProductDetailsStrategy.setText(name + "攻略");
        productDetailsLimitTv.setText("借款额度: "+limit);
        productDetailsDeadlineTv.setText("借款期限: "+deadline);
        productDetailsCostTv.setText("借款费用: "+cost);
        productDetailsSpeedTv.setText("下款速度: "+speed);
        productDetailsDifficultyTv.setText("审批难度: "+difficulty);
        productDetailsDemand1Tv.setText("1 "+demand1);
        productDetailsDemand2Tv.setText("2 "+demand2);
        productDetailsTips1Tv.setText("3 "+tips1);
        productDetailsTips2Tv.setText("4 "+tips2);
    }

    private void getData() {
        Intent intent = getIntent();
        prdListProduct = (Product.PrdListProduct) intent.getSerializableExtra("PrdListProduct");
        name = prdListProduct.getName();
        limit = prdListProduct.getLines();
        deadline = prdListProduct.getTimeLimit();
        cost = prdListProduct.getRate();
        speed = prdListProduct.getSpeed();
        difficulty = prdListProduct.getDifficulty();
        demand1 = prdListProduct.getDemand1();
        demand2 = prdListProduct.getDemand2();
        tips1 = prdListProduct.getTips1();
        tips2 = prdListProduct.getTips2();
        url = prdListProduct.getLink();
        logoUrl = prdListProduct.getLogo();
        proid = prdListProduct.getUid();
        // 加载图片
        Glide.with(this).load(Constants.piURL + logoUrl).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivProductDetails);
    }


    // 控件监听事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_product_details_apply:
                if (DeviceUtil.IsNetWork(this) == false) {
                    Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 保存浏览记录
                //浏览记录
                new BrowsingHistory().execute(proid,"999");
                Intent intent = new Intent(ProductDetailsActivity.this, WebViewActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", name);
                startActivity(intent);
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        // 友盟统计
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 友盟统计
        MobclickAgent.onPause(this);
    }
}
