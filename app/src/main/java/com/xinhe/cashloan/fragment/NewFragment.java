package com.xinhe.cashloan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.activity.LoginActivity;
import com.xinhe.cashloan.activity.ProductDetailsActivity;
import com.xinhe.cashloan.activity.ProductListActivity;
import com.xinhe.cashloan.activity.WebViewActivity;
import com.xinhe.cashloan.activity.WebViewTitleActivity;
import com.xinhe.cashloan.adapter.MainProductAdapter;
import com.xinhe.cashloan.adapter.TenProductAdapter;
import com.xinhe.cashloan.biz.BrowsingHistory;
import com.xinhe.cashloan.biz.ClickHistory;
import com.xinhe.cashloan.entity.ImagerBean;
import com.xinhe.cashloan.entity.MyMessage;
import com.xinhe.cashloan.entity.Product;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.DeviceUtil;
import com.xinhe.cashloan.util.ExceptionUtil;
import com.xinhe.cashloan.util.FixedSpeedScroller;
import com.xinhe.cashloan.util.Utill;
import com.xinhe.cashloan.view.MyProgressDialog;
import com.xinhe.cashloan.view.VerticalViewPager;
import com.xinhe.cashloan.view.pullableview.PullToRefreshLayout;
import com.xinhe.cashloan.view.pullableview.PullableRecycleView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;


/**
 *
 */
public class NewFragment extends Fragment {

    @Bind(R.id.new_fragment_rv)
    PullableRecycleView newFragmentRv;
    @Bind(R.id.new_refresh_view)
    PullToRefreshLayout newRefreshView;
    private View view;
    private ImagerBean imagerBean;
    private ArrayList<Product.PrdListProduct> tenProducts = new ArrayList<Product.PrdListProduct>();
    private ArrayList<Product.PrdListProduct> newProducts = new ArrayList<Product.PrdListProduct>();
    private ArrayList<MyMessage> messagess = new ArrayList<MyMessage>();
    private MyVpAdapger vpAdapter;
    private Timer timer;
    private int currentIndex;
    // 消息滚动滚动
    Handler h = new Handler() {
        public void handleMessage(Message msg) {
            newFragmentVvp.setCurrentItem(currentIndex);// 设置此次要显示的pager

        }

    };
    private String[] phone = {"3", "5", "8", "7"};


    private BGABanner newFragmentBanner;
    private VerticalViewPager newFragmentVvp;
    private MainProductAdapter adapter1;
    private View headerView;
    private TextView loanCountTv;
    private RecyclerView tenProductRv;
    private TenProductAdapter tenAdapter;
    private LinearLayout ll1;
    private ArrayList<Product.PrdListProduct> bestProduct;
    private LinearLayout ll2;
    private LinearLayout ll3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载界面
        view = View.inflate(getActivity(), R.layout.fragment_new, null);
        try {
            ButterKnife.bind(this, view);
            //初始化头部
            initHeader();
            //设置控件
            setViews();
            //设置监听
            setListener();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }

        ButterKnife.bind(this, view);
        return view;
    }

    private void initHeader() {

        headerView = getActivity().getLayoutInflater().inflate(R.layout.header_layout, null);
        newFragmentBanner = (BGABanner) headerView.findViewById(R.id.new_fragment_banner);
        newFragmentVvp = (VerticalViewPager) headerView.findViewById(R.id.new_fragment_vvp);
        loanCountTv = (TextView) headerView.findViewById(R.id.new_fragment_loan_count);
        tenProductRv = (RecyclerView) headerView.findViewById(R.id.main_rv);
        ll1 = (LinearLayout) headerView.findViewById(R.id.main_fragment_ll1);
        ll2 = (LinearLayout) headerView.findViewById(R.id.main_fragment_ll2);
        ll3 = (LinearLayout) headerView.findViewById(R.id.main_fragment_ll3);

        //贷款大全
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击记录
                ClickHistory.saveClickHistory("3button_1",getContext());
                Intent intent = new Intent();
                //对应BroadcastReceiver中intentFilter的action
                intent.setAction(Constants.INTENT_EXTRA_MAIN_FRAGMENT_CLICK);
                //发送广播
                getActivity().sendBroadcast(intent);
            }
        });

        //精品推荐
        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击记录
                ClickHistory.saveClickHistory("3button_2",getContext());
                Intent intent=new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("PrdListProduct",bestProduct.get(0));
                startActivity(intent);
            }
        });

        //办信用卡
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.isLogin){
                    //点击记录
                    ClickHistory.saveClickHistory("3button_3",getContext());
                    try {
                        String channel = URLEncoder.encode(Constants.channel, "UTF-8");
                        channel=channel.replace("%","%25");
                        Intent intent = new Intent(getContext(), WebViewTitleActivity.class);
                        intent.putExtra("url", "http://www.shoujijiekuan.com/Credit/index.html?terminal="+channel+"&source="+Constants.channel1+"&uid="+MyApplication.userId);
                        startActivity(intent);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        /**
         * 调整viewpager的切换速度
         */
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller( newFragmentVvp.getContext( ) );
            mScroller.set( newFragmentVvp, scroller);
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }

//        //借款人数
//        int l = (int) (System.currentTimeMillis()/10000)-140000000;
//        loanCountTv.setText("已有"+l+"人申请");
        //借款人数
        int l = (int)((System.currentTimeMillis()/1000)%86400)/2;
        loanCountTv.setText("今日已有"+l+"人申请");

        /**
         * 初始化banner
         */
        newFragmentBanner.setDelegate(new BGABanner.Delegate<ImageView, String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                if (MyApplication.isLogin){
                    startActivity(new Intent(getContext(), WebViewActivity.class).putExtra("url", imagerBean.getDaohang().get(position).getLink()));
                }else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        newFragmentBanner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                Glide.with(getActivity())
                        .load(model)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .dontAnimate()
                        .into(itemView);
            }
        });


        //轮播图
        if (null != MyApplication.imagerBean) {
            imagerBean = MyApplication.imagerBean;
            setBunder();
        } else {
            getImageBean();
        }

        //分类
        if(null!=MyApplication.bestProduct){
            bestProduct = MyApplication.bestProduct.getPrdList();
        }else {
            getBestProduct();
        }

        //十个产品
        if(null!=MyApplication.tenProduct){
            tenProducts=MyApplication.tenProduct.getPrdList();
            //消息
            getMessages();
        }else {
            getTenProduct();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        tenProductRv.setLayoutManager(layoutManager);
        tenAdapter=new TenProductAdapter(tenProducts);
        tenProductRv.setAdapter(tenAdapter);


        //消息
        newFragmentVvp.setPageTransformer(false,new DefaultTransformer());
        vpAdapter = new MyVpAdapger();
        newFragmentVvp.setAdapter(vpAdapter);// 配置pager页
        startCycle();
    }

    //精品推荐
    private void getBestProduct() {
        if (DeviceUtil.IsNetWork(getContext()) == false) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "GetProduct";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("sAppName", Constants.appName);
                rpc.addProperty("sPage", "55");
                rpc.addProperty("channel", "3");
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String str = object.getProperty("GetProductResult").toString();

                    if (!TextUtils.isEmpty(str) && !str.startsWith("1")&& !str.startsWith("2")) {
                        Gson gson = new Gson();
                        Product product = gson.fromJson(str, Product.class);
                        MyApplication.bestProduct=product;
                        bestProduct=product.getPrdList();
                    }
                } catch (Exception e) {
                    ExceptionUtil.handleException(e);
                }
            }
        }).start();
    }


    private void setViews() {
        if (DeviceUtil.IsNetWork(getContext()) == false) {
            Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
        }

        //新品
        if(null != MyApplication.newProduct){
            newProducts=MyApplication.newProduct.getPrdList();
        }else {
            getNewProduct();
        }
        adapter1 = new MainProductAdapter(newProducts);
        newFragmentRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter1.addHeaderView(headerView, 0);
        newFragmentRv.setAdapter(adapter1);
    }

    private void getNewProduct() {
        if (DeviceUtil.IsNetWork(getContext()) == false) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "NewProduct";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
//                rpc.addProperty("sAppName", Constants.appName);
//                rpc.addProperty("sPage", "3");
                rpc.addProperty("channel", "3");
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    final String str = object.getProperty("NewProductResult").toString();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2")) {
                                Gson gson = new Gson();
                                Product product = gson.fromJson(str, Product.class);
                                newProducts.clear();
                                newProducts.addAll(product.getPrdList());
                                adapter1.notifyDataSetChanged();
                                //消息刷新
                                getMessages();
                                if (null != timer) {
                                    timer.cancel();
                                }
                                vpAdapter.notifyDataSetChanged();
                                startCycle();

                            } else {
                                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ExceptionUtil.handleException(e);
                            Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

    //VerticalViewPager的切换
    public class DefaultTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            float alpha = 0;
            if (0 <= position && position <= 1) {
                alpha = 1 - position;
            } else if (-1 < position && position < 0) {
                alpha = position + 1;
            }
            //view.setAlpha(alpha);
            view.setTranslationX(view.getWidth() * -position);
            float yPosition = position * view.getHeight();
            view.setTranslationY(yPosition);
        }
    }

    private void setListener() {
        //点击某一项
        newFragmentRv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent=new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("PrdListProduct",newProducts.get(position));
                startActivity(intent);
//                //浏览记录
//                new BrowsingHistory().execute(newProducts.get(position).getUid(),"0");
                //点击记录
                ClickHistory.saveClickHistory("new_"+(position+1),getContext());
            }
        });
        //刷新
        newRefreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                if (DeviceUtil.IsNetWork(getContext()) == false) {
                    Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    return;
                }
                //轮播图
                getImageBean();
                //借款人数
                int l = (int)((System.currentTimeMillis()/1000)%86400)/2;
                loanCountTv.setText("今日已有"+l+"人申请");

                //精品推荐
                getBestProduct();

                //十个产品，单写在这是为了刷新
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String URL = Constants.URL;
                        String nameSpace = Constants.nameSpace;
                        String method_Name = "GetProduct";
                        String SOAP_ACTION = nameSpace + method_Name;
                        SoapObject rpc = new SoapObject(nameSpace, method_Name);
                        rpc.addProperty("sAppName", Constants.appName);
                        rpc.addProperty("sPage", "66");
                        rpc.addProperty("channel", "3");
                        HttpTransportSE transport = new HttpTransportSE(URL);
                        transport.debug = true;
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.bodyOut = rpc;
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(rpc);
                        try {
                            transport.call(SOAP_ACTION, envelope);
                            SoapObject object = (SoapObject) envelope.bodyIn;
                            final String str = object.getProperty("GetProductResult").toString();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2")) {
                                        Gson gson = new Gson();
                                        Product product = gson.fromJson(str, Product.class);
                                        tenProducts.clear();
                                        tenProducts.addAll(product.getPrdList());
                                        tenAdapter.notifyDataSetChanged();
                                        //消息刷新
                                        getMessages();
                                        if (null != timer) {
                                            timer.cancel();
                                        }
                                        vpAdapter.notifyDataSetChanged();
                                        startCycle();

                                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                    } else {
                                        Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                                    }
                                }
                            });

                        } catch (final Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ExceptionUtil.handleException(e);
                                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).start();
                //新品
                getNewProduct();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
        //十个产品
        tenProductRv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent=new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("PrdListProduct",tenProducts.get(position));
                startActivity(intent);
//                new BrowsingHistory().execute(tenProducts.get(position).getUid(),"0");
                //点击记录
                ClickHistory.saveClickHistory("tuijian_"+(position+1),getContext());
            }
        });
    }

    private void getProductList(final String position, final String name) {
        final MyProgressDialog dialog = new MyProgressDialog(getContext(), "获取中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "GetProduct";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("sAppName", Constants.appName);
                rpc.addProperty("sPage", position);
                rpc.addProperty("channel", "3");
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    final String str = object.getProperty("GetProductResult").toString();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2")) {
                                Gson gson = new Gson();
                                Product product = gson.fromJson(str, Product.class);
                                Intent intent = new Intent(getContext(), ProductListActivity.class);
                                intent.putExtra("name",name);
                                intent.putExtra("product",product);
                                startActivity(intent);
                                //点击记录
                                ClickHistory.saveClickHistory(("type_"+position),getContext());
                            } else {
                                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });

                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ExceptionUtil.handleException(e);
                            dialog.dismiss();
                            Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }


    /**
     * 循环线程
     */
    private void startCycle() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (newFragmentVvp != null) {
                    if (newFragmentVvp.getCurrentItem() == messagess.size() - 1) {
                        currentIndex = 0;
                    } else {
                        currentIndex = newFragmentVvp.getCurrentItem() + 1;// 下一个页
                    }
                    h.sendEmptyMessage(0);// 在此线程中，不能操作ui主线程
                }
            }
        }, 4000, 4000);

    }

    private void getMessages() {
        if (tenProducts.size() > 0) {
            for (int i = 0; i <= 20; i++) {
                MyMessage message=new MyMessage();
                int s=new Random().nextInt(tenProducts.size() - 1);
                message.name = tenProducts.get(s).getName();
                message.logo = Constants.piURL+tenProducts.get(s).getLogo();
                messagess.add(message);
            }
        }
    }


    private void getTenProduct() {
        if (DeviceUtil.IsNetWork(getContext()) == false) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "GetProduct";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("sAppName", Constants.appName);
                rpc.addProperty("sPage", "66");
                rpc.addProperty("channel", "3");
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    final String str = object.getProperty("GetProductResult").toString();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2")) {
                                Gson gson = new Gson();
                                Product product = gson.fromJson(str, Product.class);
                                tenProducts.clear();
                                tenProducts.addAll(product.getPrdList());
                                tenAdapter.notifyDataSetChanged();
                                //消息刷新
                                getMessages();
                                if (null != timer) {
                                    timer.cancel();
                                }
                                vpAdapter.notifyDataSetChanged();
                                startCycle();
                            } else {
                                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ExceptionUtil.handleException(e);
                            Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }


    /**
     * header 滚动
     */
    private void setBunder() {
        final ArrayList<String> arr = new ArrayList<>();
        final ArrayList<ImagerBean.DaohangProduct> Data = imagerBean.getDaohang();
        for (ImagerBean.DaohangProduct s : Data) {
            arr.add(Constants.piURL + s.getAdvpath());
        }
        newFragmentBanner.setData(arr, null);
    }

    //获取banner
    private void getImageBean() {

        if (DeviceUtil.IsNetWork(getContext()) == false) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "Daohang";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    final String result = object.getProperty("DaohangResult").toString();
                    if (!TextUtils.isEmpty(result)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                imagerBean = gson.fromJson(result, ImagerBean.class);
                                Collections.sort(imagerBean.getDaohang());
                                setBunder();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }


    // ViewPager的适配器
    private class MyVpAdapger extends PagerAdapter {

        @Override
        public int getCount() {
            return messagess.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.new_product_vvp_item, null);
            ImageView iv1 = (ImageView) view.findViewById(R.id.show_loan_iv);
            TextView tv1 = (TextView) view.findViewById(R.id.show_loan_tv1);
            TextView tv2 = (TextView) view.findViewById(R.id.new_product_vvp_item_tv2);
            TextView tv3 = (TextView) view.findViewById(R.id.new_product_vvp_item_tv3);
            TextView tv4 = (TextView) view.findViewById(R.id.new_product_vvp_item_tv4);
            TextView tv5 = (TextView) view.findViewById(R.id.new_product_vvp_item_tv5);
            String phone1 = "1" + phone[new Random().nextInt(4)] + new Random().nextInt(10) + "****" + (new Random().nextInt(8999) + 1000);
            String money = (new Random().nextInt(10) + 1) * 1000 + "";
            tv2.setText(phone1);
            tv1.setText(messagess.get(position).name);
            tv4.setText(money);
            Glide.with(getContext()).load(messagess.get(position).logo).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv1);

            // 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(view);
            }
            container.addView(view);
            return view;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
