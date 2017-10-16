package com.xinhe.cashloan.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.adapter.ProductAdapter;
import com.xinhe.cashloan.biz.BrowsingHistory;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.ExceptionUtil;
import com.xinhe.cashloan.util.Utill;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tantan on 2017/9/29.
 */

public class ProductListActivity extends AppCompatActivity {
    @Bind(R.id.product_list_rv)
    RecyclerView productListRv;
    @Bind(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    private Product product;
    private ProductAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);
        ButterKnife.bind(this);
        try {
            // 4.4版本以上设置 全屏显示，状态栏在界面上方
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                // 透明导航栏
                // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            // 设置顶部控件不占据状态栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                LinearLayout barLl = (LinearLayout) findViewById(R.id.product_list_top);
                barLl.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) barLl.getLayoutParams();
                ll.height = Utill.getStatusBarHeight(this);
                ll.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                barLl.setLayoutParams(ll);
            }
            initActionBar();
            setViews();
            setListener();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }

    }

    private void setListener() {
        productListRv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent=new Intent(ProductListActivity.this, WebViewActivity.class);
                intent.putExtra("url",product.getPrdList().get(position).getLink());
                intent.putExtra("title",product.getPrdList().get(position).getName());
                startActivity(intent);
                new BrowsingHistory().execute(product.getPrdList().get(position).getUid(),"0");
            }
        });
    }

    private void setViews() {
        Intent intent = getIntent();
        myToolbarTv.setText(intent.getStringExtra("name"));
        product= (Product) intent.getSerializableExtra("product");
        //创建默认的线性LayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        productListRv.setLayoutManager(mLayoutManager);
        if (null!=product){
            adapter = new ProductAdapter(product.getPrdList());
            productListRv.setAdapter(adapter);
        }

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
}
