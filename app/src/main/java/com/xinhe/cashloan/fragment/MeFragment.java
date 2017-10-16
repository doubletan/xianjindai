package com.xinhe.cashloan.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.activity.WebViewActivity;
import com.xinhe.cashloan.adapter.CreditProductAdapter;
import com.xinhe.cashloan.biz.BrowsingHistory;
import com.xinhe.cashloan.entity.CreditProduct;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.DeviceUtil;
import com.xinhe.cashloan.util.ExceptionUtil;
import com.xinhe.cashloan.util.Utill;
import com.xinhe.cashloan.view.MyProgressDialog;
import com.xinhe.cashloan.view.pullableview.PullToRefreshLayout;
import com.xinhe.cashloan.view.pullableview.PullableListView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MeFragment extends Fragment {


    @Bind(R.id.pull_icon)
    ImageView pullIcon;
    @Bind(R.id.refreshing_icon)
    ImageView refreshingIcon;
    @Bind(R.id.state_tv)
    TextView stateTv;
    @Bind(R.id.state_iv)
    ImageView stateIv;
    @Bind(R.id.head_view)
    RelativeLayout headView;
    @Bind(R.id.new_fragment_lv)
    PullableListView newFragmentLv;
    @Bind(R.id.pullup_icon)
    ImageView pullupIcon;
    @Bind(R.id.loading_icon)
    ImageView loadingIcon;
    @Bind(R.id.loadstate_tv)
    TextView loadstateTv;
    @Bind(R.id.loadstate_iv)
    ImageView loadstateIv;
    @Bind(R.id.loadmore_view)
    RelativeLayout loadmoreView;
    @Bind(R.id.new_refresh_view)
    PullToRefreshLayout newRefreshView;
    private View view;
    private WebView myWebView;
    private ProgressBar bar;
    private ArrayList<CreditProduct.CardListProduct> creditProducts=new ArrayList<>();
    private CreditProductAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.me_fragment, null);
        try {
            ButterKnife.bind(this, view);
            // 设置顶部控件不占据状态栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                LinearLayout barLl = (LinearLayout) view.findViewById(R.id.me_fragment_top);
                barLl.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) barLl.getLayoutParams();
                ll.height = Utill.getStatusBarHeight(getContext());
                ll.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                barLl.setLayoutParams(ll);
            }
            //初始化控件
            initViews();
            //设置监听
            setListeners();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }

        return view;
    }

    private void initViews() {
        //产品
        getProduct();
        adapter = new CreditProductAdapter(getContext(),creditProducts);
        newFragmentLv.setAdapter(adapter);
    }

    private void setListeners() {

        newFragmentLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url",creditProducts.get(position).getClink());
                intent.putExtra("title",creditProducts.get(position).getCname());
                startActivity(intent);
                new BrowsingHistory().execute(creditProducts.get(position).getUid(),"4");
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
                //产品
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String URL = Constants.URL;
                        String nameSpace = Constants.nameSpace;
                        String method_Name = "CreditCardList";
                        String SOAP_ACTION = nameSpace + method_Name;
                        SoapObject rpc = new SoapObject(nameSpace, method_Name);
                        rpc.addProperty("channel", Constants.qudao);
                        HttpTransportSE transport = new HttpTransportSE(URL);
                        transport.debug = true;
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.bodyOut = rpc;
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(rpc);
                        try {
                            transport.call(SOAP_ACTION, envelope);
                            SoapObject object = (SoapObject) envelope.bodyIn;
                            final String str = object.getProperty("CreditCardListResult").toString();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2,")) {
                                        Gson gson = new Gson();
                                        CreditProduct creditProduct = gson.fromJson(str, CreditProduct.class);
                                        creditProducts.clear();
                                        creditProducts.addAll(creditProduct.getCardList());
                                        adapter.notifyDataSetChanged();
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
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
    }

    private void getProduct() {
        if (DeviceUtil.IsNetWork(getContext()) == false) {
            Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        final MyProgressDialog dialog = new MyProgressDialog(getContext(), "获取中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
                String nameSpace = Constants.nameSpace;
                String method_Name = "CreditCardList";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("channel", Constants.qudao);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    final String str = object.getProperty("CreditCardListResult").toString();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(str) && !str.startsWith("1,") && !str.startsWith("2,")) {
                                Gson gson = new Gson();
                                CreditProduct creditProduct = gson.fromJson(str, CreditProduct.class);
                                creditProducts.clear();
                                creditProducts.addAll(creditProduct.getCardList());
                                adapter.notifyDataSetChanged();
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
