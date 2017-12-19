package com.xinhe.cashloan.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.umeng.analytics.MobclickAgent;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.CheckUtil;
import com.xinhe.cashloan.util.CodeUtils;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.DeviceUtil;
import com.xinhe.cashloan.util.ExceptionUtil;
import com.xinhe.cashloan.util.GetMyKey;
import com.xinhe.cashloan.util.IDCardCheckUtil;
import com.xinhe.cashloan.util.IsChineseUtil;
import com.xinhe.cashloan.util.SPUtils;
import com.xinhe.cashloan.util.Utill;
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

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_et1)
    EditText loginEt1;
    @Bind(R.id.login_et2)
    EditText loginEt2;
    @Bind(R.id.login_code_btn)
    TextView loginCodeBtn;
    @Bind(R.id.login_rl2)
    RelativeLayout loginRl2;
    @Bind(R.id.login_btn)
    Button loginBtn;
    @Bind(R.id.login_cb)
    CheckBox loginCb;
    @Bind(R.id.login_agreement)
    TextView loginAgreement;
    @Bind(R.id.login_ll1)
    LinearLayout loginLl1;
    @Bind(R.id.login_name_tv)
    TextView loginNameTv;
    @Bind(R.id.login_name_et)
    EditText loginNameEt;
    @Bind(R.id.login_name_rl)
    RelativeLayout loginNameRl;
    @Bind(R.id.login_ID_tv)
    TextView loginIDTv;
    @Bind(R.id.login_ID_et)
    EditText loginIDEt;
    @Bind(R.id.login_ID_rl)
    RelativeLayout loginIDRl;
    @Bind(R.id.login_ll2)
    LinearLayout loginLl2;
    @Bind(R.id.login_sb)
    SeekBar loginSb;
    private String phone;
    private TimeCount time;
    private String sysId;
    private String getCode;
    private String savePhone;
    private String code;
    private long mLastBackTime;
    private boolean isNewUser = false;
    private CodeUtils codeUtils;
    private String yanZhengCode;
    private String etYanZhengCode;
    private String yanZhengResult;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private ImageView ivVerify;
    private EditText etVerify;
    private AlertDialog alertDialog;
    private String myName;
    private String myID;
    private String province;
    private String city;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 4.4版本以上设置 全屏显示，状态栏在界面上方
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // 设置顶部控件不占据状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout barLl = (LinearLayout) findViewById(R.id.rl_login_top);
            barLl.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) barLl.getLayoutParams();
            ll.height = Utill.getStatusBarHeight(this);
            ll.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            barLl.setLayoutParams(ll);
        }

        try {
            //把当前activity加入到集合里
            MyApplication.getApp().addToList(this);
            //设置控件
            setViews();
            //设置监听
            setListener();
            /**
             * 位置
             */
            //声明LocationClient类
            mLocationClient = new LocationClient(getApplicationContext());
            //注册监听函数
            mLocationClient.registerLocationListener(myListener);
            location();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setListener() {
        loginSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (100!=seekBar.getProgress()){
                    seekBar.setProgress(0);
                }else {
                    if (TextUtils.isEmpty(loginEt1.getText().toString().trim())){
                        seekBar.setProgress(0);
                        Toast.makeText(LoginActivity.this,"请输入手机号",Toast.LENGTH_LONG).show();
                    }else if (TextUtils.isEmpty(savePhone)){
                        seekBar.setProgress(0);
                        Toast.makeText(LoginActivity.this,"请获取验证码验证",Toast.LENGTH_LONG).show();
                    }else if (loginCb.isChecked()){
                        if (isNewUser){
                            login();
                        }else {
                            changeInformation();
                        }
                    }else {
                        seekBar.setProgress(0);
                        Toast.makeText(LoginActivity.this,"请勾选服务协议",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void changeInformation() {
        //输入框不可编辑
        loginEt1.setFocusable(false);
        loginEt1.setFocusableInTouchMode(false);
        loginEt2.setFocusable(false);
        loginEt2.setFocusableInTouchMode(false);
        //验证码按钮隐藏
        loginCodeBtn.setVisibility(View.GONE);
        //切换布局
        loginLl1.setVisibility(View.GONE);
        loginLl2.setVisibility(View.VISIBLE);
    }

    private void setViews() {
        if (!TextUtils.isEmpty(MyApplication.phone)){
            loginEt1.setText(MyApplication.phone);
        }
        // 构造CountDownTimer对象
        time = new TimeCount(60000, 1000);
    }

    @OnClick({R.id.login_btn, R.id.login_code_btn, R.id.login_agreement})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_agreement:
                try {
                    String companyName = URLEncoder.encode(Constants.companyName, "UTF-8");
                    String appName = URLEncoder.encode(Constants.appName, "UTF-8");
                    companyName=companyName.replace("%","%25");
                    appName=appName.replace("%","%25");
                    String url = "http://www.shoujijiekuan.com/GVRP/index.html?com_name=" + companyName + "&app_name=" + appName;
                    Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", "现金贷服务协议");
                    startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.login_btn:
                submitInformation();
                break;
            case R.id.login_code_btn:
                phone = loginEt1.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!CheckUtil.isMobile(phone)) {
                    Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
                    return;
                }
                showVerifyDialog();
                break;
        }
    }

    //提交信息
    private void submitInformation() {

        //权限检查
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    Constants.PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }

        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        //姓名
        myName = loginNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(etYanZhengCode)) {
            Toast.makeText(this, "请输入您的姓名", Toast.LENGTH_LONG).show();
            return;
        }
        if (!IsChineseUtil.checkNameChese(myName)) {
            Toast.makeText(this, "请输入中文姓名", Toast.LENGTH_LONG).show();
            return;
        }
        //身份证号
        myID = loginIDEt.getText().toString().trim();
        if (TextUtils.isEmpty(etYanZhengCode)) {
            Toast.makeText(this, "请输入您的身份证号", Toast.LENGTH_LONG).show();
            return;
        }
        String chekID = "id";
        try {
            chekID = IDCardCheckUtil.IDCardValidate(myID);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!"".equals(chekID)) {
            Toast.makeText(this, "身份证号输入有误", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject js1 = new JSONObject();
        final JSONObject js2 = new JSONObject();
        try {
            js1.put("xjdid", MyApplication.userId);
            js1.put("name", myName);
            js1.put("idcard", myID);
            js1.put("province", province);
            js1.put("city", city);
            js1.put("address", address);
            js2.put("Input", js1);
        } catch (JSONException e) {
            ExceptionUtil.handleException(e);
        }

        final MyProgressDialog dialog = new MyProgressDialog(this, "登陆中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL1;
                String nameSpace = Constants.nameSpace;
                String method_Name = "UserDetailInput1";
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
                    String result = object.getProperty("UserDetailInput1Result").toString();
                    if (!TextUtils.isEmpty(result) && result.startsWith("0")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, ChooseInformationActivity.class);
                                startActivityForResult(intent,Constants.LOGINACTIVITY_TO_CHOOSEINFOMATION);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    }

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 新用户登录
     */
    private void login() {
        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            loginSb.setProgress(0);
            return;
        }

        phone = loginEt1.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            loginSb.setProgress(0);
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            loginSb.setProgress(0);
            return;
        }

        if (TextUtils.isEmpty(getCode)) {
            Toast.makeText(this, "请获取手机验证码", Toast.LENGTH_LONG).show();
            loginSb.setProgress(0);
            return;
        }

        code = loginEt2.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_LONG).show();
            loginSb.setProgress(0);
            return;
        }
        if (!code.equals(getCode)) {
            Toast.makeText(this, "验证码输入错误", Toast.LENGTH_LONG).show();
            loginSb.setProgress(0);
            return;
        }
        if (!phone.equals(savePhone)) {
            Toast.makeText(this, "手机号与验证码不匹配", Toast.LENGTH_LONG).show();
            loginSb.setProgress(0);
            return;
        }

        JSONObject js1 = new JSONObject();
        final JSONObject js2 = new JSONObject();
        try {
            js1.put("username", phone);
            js1.put("password", "");
            js1.put("channel", Constants.channel);
            js1.put("qudao", Constants.channel1);
            js2.put("Register", js1);
        } catch (JSONException e) {
            ExceptionUtil.handleException(e);
        }

        final MyProgressDialog dialog = new MyProgressDialog(this, "注册中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL1;
                String nameSpace = Constants.nameSpace;
                String method_Name = "QuickLgn";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("strJson", js2.toString());
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String result = object.getProperty("QuickLgnResult").toString();
                    if (!TextUtils.isEmpty(result) && result.startsWith("0,")) {
                        MyApplication.userId = result.substring(2);
                        SPUtils.put(LoginActivity.this, "userId", MyApplication.userId);
                        SPUtils.put(LoginActivity.this, "phone", savePhone);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                changeInformation();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                                loginSb.setProgress(0);
                                dialog.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                            loginSb.setProgress(0);
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();

    }

    private void sendCode() {
        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        etYanZhengCode = etVerify.getText().toString().trim();

        if (TextUtils.isEmpty(etYanZhengCode)) {
            Toast.makeText(this, "请输入图片里的结果", Toast.LENGTH_LONG).show();
            return;
        }

        if (!yanZhengResult.equals(etYanZhengCode)) {
            Toast.makeText(this, "图片结果输入有误", Toast.LENGTH_LONG).show();
            return;
        }

        alertDialog.dismiss();

        JSONObject js1 = new JSONObject();
        final JSONObject js2 = new JSONObject();
        try {
            js1.put("username", phone);
            js1.put("password", GetMyKey.getKey());
            js1.put("channel", Constants.channel);
            js1.put("qudao", Constants.channel1);
            js2.put("Register", js1);
        } catch (JSONException e) {
            ExceptionUtil.handleException(e);
        }

        final MyProgressDialog dialog = new MyProgressDialog(this, "用户验证中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL1;
                String nameSpace = Constants.nameSpace;
                String method_Name = "QuickLgnMsg";
                String SOAP_ACTION = nameSpace + method_Name;
                SoapObject rpc = new SoapObject(nameSpace, method_Name);
                rpc.addProperty("strJson", js2.toString());
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.debug = true;
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.bodyOut = rpc;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                try {
                    transport.call(SOAP_ACTION, envelope);
                    SoapObject object = (SoapObject) envelope.bodyIn;
                    String result = object.getProperty("QuickLgnMsgResult").toString();
                    if (!TextUtils.isEmpty(result) && result.startsWith("0,")) {
                        getCode = result.substring(2);
                        savePhone = phone;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //显示验证码
                                loginRl2.setVisibility(View.VISIBLE);
                                //新用户
                                isNewUser=true;
                                // 开始计时
                                time.start();
                                Toast.makeText(LoginActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    } else if (!TextUtils.isEmpty(result) && result.startsWith("1,")) {
                        MyApplication.userId = result.substring(2);
                        savePhone = phone;
                        SPUtils.put(LoginActivity.this, "userId", MyApplication.userId);
                        SPUtils.put(LoginActivity.this, "phone", savePhone);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //老用户
                                isNewUser=false;
                                loginCodeBtn.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "验证成功", Toast.LENGTH_LONG).show();
                                dialog.dismiss();

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    }

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();

    }

    private void initYanzheng() {
        codeUtils = CodeUtils.getInstance();
        Bitmap bitmap = codeUtils.createBitmap();
        ivVerify.setImageBitmap(bitmap);
        yanZhengCode = codeUtils.getCode();
        yanZhengResult = codeUtils.getResult() + "";
    }

    /*
     *
     *
     * 弹出对话框的步骤 1.创建alertDialog的builder. 2.要给builder设置属行, 对话框的内容,样式,按钮
     * 3.通过builder 创建个对话框 4.对话框show()出来
     */
    protected void showVerifyDialog() {
        alertDialog = new AlertDialog.Builder(this, R.style.CustomDialog).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        /**
         * 下面三行可以让对话框里的输入框可以输入
         */
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.verify, null);
        alertDialog.setView(layout);

        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.verify);
        ivVerify = (ImageView) window.findViewById(R.id.verify_iv);
        etVerify = (EditText) window.findViewById(R.id.verify_et);
        Button btn = (Button) window.findViewById(R.id.verify_btn);
        ivVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initYanzheng();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });
        initYanzheng();
    }

    private void location() {
        //权限检查
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    Constants.PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明


        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true


        mLocationClient.start();
    }

    //位置监听
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明


            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            province = location.getProvince();    //获取省份
            city = location.getCity();    //获取城市
            address = location.getDistrict()+location.getStreet();    //获取区县
//            String street = location.getStreet();    //获取街道信息
        }
    }


    // 定义一个倒计时的内部类
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            loginCodeBtn.setText("重新发送");
            loginCodeBtn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            loginCodeBtn.setClickable(false);
            loginCodeBtn.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 109:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    location();
                } else {
                    Toast.makeText(this, "位置权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mLastBackTime) < 2000) {
            finish();
            MyApplication.getApp().onTerminate();
        } else {
            mLastBackTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 110:
                if (-1==resultCode){
                    setResult(-1);
                    finish();
                    overridePendingTransition(R.anim.login_in, R.anim.login_out);
                }else if (1==resultCode){
                    finish();
                    MyApplication.getApp().onTerminate();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
