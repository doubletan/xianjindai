package com.xinhe.cashloan.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.DeviceUtil;
import com.xinhe.cashloan.util.ExceptionUtil;
import com.xinhe.cashloan.util.IDCardCheckUtil;
import com.xinhe.cashloan.util.IsChineseUtil;
import com.xinhe.cashloan.util.SPUtils;
import com.xinhe.cashloan.view.MyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseInformationActivity extends AppCompatActivity {

    @Bind(R.id.choose_creditcard_rg)
    RadioGroup chooseCreditcardRg;
    @Bind(R.id.choose_house_rg)
    RadioGroup chooseHouseRg;
    @Bind(R.id.choose_car_rg)
    RadioGroup chooseCarRg;
    @Bind(R.id.choose_profession_rg)
    RadioGroup chooseProfessionRg;
    @Bind(R.id.choose_btn)
    Button chooseBtn;
    @Bind(R.id.choose_agreement)
    TextView chooseAgreement;
    @Bind(R.id.choose_cb)
    CheckBox chooseCb;
    private String creditcard1;
    private String house1;
    private String car1;
    private String profession1;
    private AlertDialog alertDialog;
    private boolean isNewUser;
    private MyProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_information);
        ButterKnife.bind(this);
        try {
            //把当前activity加入到集合里
            MyApplication.getApp().addToList(this);
            //初始化
            setViews();
            //监听
            setListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //设置监听
    private void setListeners() {
        chooseCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    chooseBtn.setClickable(true);
                    chooseBtn.setBackgroundResource(R.drawable.btn_selector);
                }else{
                    chooseBtn.setClickable(false);
                    chooseBtn.setBackgroundResource(R.drawable.btn_selector_true);
                }
            }
        });
    }

    //初始化
    private void setViews() {

        Intent intent=getIntent();
        isNewUser=intent.getBooleanExtra("isNewUser",false);
    }

    @OnClick({R.id.choose_btn, R.id.choose_agreement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.choose_btn:
                switch (chooseCreditcardRg.getCheckedRadioButtonId()){
                    case R.id.choose_creditcard_rb1:
                        creditcard1="1";
                        break;
                    case R.id.choose_creditcard_rb2:
                        creditcard1="2";
                        break;
                }
                switch (chooseHouseRg.getCheckedRadioButtonId()){
                    case R.id.choose_house_rb1:
                        house1="1";
                        break;
                    case R.id.choose_house_rb2:
                        house1="2";
                        break;
                    case R.id.choose_house_rb3:
                        house1="3";
                        break;
                }
                switch (chooseCarRg.getCheckedRadioButtonId()){
                    case R.id.choose_car_rb1:
                        car1="1";
                        break;
                    case R.id.choose_car_rb2:
                        car1="2";
                        break;
                    case R.id.choose_car_rb3:
                        car1="3";
                        break;
                }
                switch (chooseProfessionRg.getCheckedRadioButtonId()){
                    case R.id.choose_profession_rb1:
                        profession1="1";
                        break;
                    case R.id.choose_profession_rb2:
                        profession1="2";
                        break;
                    case R.id.choose_profession_rb3:
                        profession1="3";
                        break;
                    case R.id.choose_profession_rb4:
                        profession1="4";
                        break;
                }
                submit();
                break;
            case R.id.choose_agreement:
                try {
                    String url = "http://www.shoujijiekuan.com/agreement/" ;
                    Intent intent = new Intent(ChooseInformationActivity.this, WebViewTitleActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //提交
    private void submit() {
        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject js1 = new JSONObject();
        final JSONObject js2 = new JSONObject();
        try {
            js1.put("xjdid", MyApplication.userId);
            js1.put("car", car1);
            js1.put("house", house1);
            js1.put("creditcard", creditcard1);
            js1.put("career", profession1);
            js2.put("Input", js1);
        } catch (JSONException e) {
            ExceptionUtil.handleException(e);
        }

        dialog = new MyProgressDialog(this, "提交中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL1;
                String nameSpace = Constants.nameSpace;
                String method_Name = "UserDetailInput2";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("strJsons", js2.toString());
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String result = object.getProperty("UserDetailInput2Result").toString();
                    if (!TextUtils.isEmpty(result) && result.startsWith("0")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long lastTime = System.currentTimeMillis()+604800000;
                                SPUtils.put(ChooseInformationActivity.this,"lasttime",lastTime);
                                MyApplication.isLogin=true;
                                if (isNewUser){
                                    getInsurance();
                                }else {
                                    Toast.makeText(ChooseInformationActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                                    finish();
                                    dialog.dismiss();
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ChooseInformationActivity.this, "提交失败", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    }

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChooseInformationActivity.this, "提交失败", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    //获取保险
    private void getInsurance() {
        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL1;
                String nameSpace = Constants.nameSpace;
                String method_Name = "PushDataInsurance";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("sXjdId", MyApplication.userId);
                rpc.addProperty("sTel", MyApplication.phone);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String result = object.getProperty("PushDataInsuranceResult").toString();
                    if (!TextUtils.isEmpty(result) && result.startsWith("0")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                finish();
                                //展示保险信息
//                                showInsuranceTip();
                            }
                        });
                    }

                } catch (Exception e) {
                }
            }
        }).start();
    }

    //保险弹出框
    /*
     *
     *
     * 弹出对话框的步骤 1.创建alertDialog的builder. 2.要给builder设置属行, 对话框的内容,样式,按钮
     * 3.通过builder 创建个对话框 4.对话框show()出来
     */
    protected void showInsuranceTip() {
        alertDialog = new AlertDialog.Builder(this, R.style.CustomDialog).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        /**
         * 下面三行可以让对话框里的输入框可以输入
         */
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.insurance_tip, null);
        alertDialog.setView(layout);

        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.insurance_tip);
//        ivVerify = (ImageView) window.findViewById(R.id.verify_iv);
//        etVerify = (EditText) window.findViewById(R.id.verify_et);
//        Button btn = (Button) window.findViewById(R.id.verify_btn);
//        ivVerify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initYanzheng();
//            }
//        });
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendCode();
//            }
//        });
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
