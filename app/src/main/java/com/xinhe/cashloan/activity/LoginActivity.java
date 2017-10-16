package com.xinhe.cashloan.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mcxtzhang.captchalib.SwipeCaptchaView;
import com.umeng.analytics.MobclickAgent;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.CheckUtil;
import com.xinhe.cashloan.util.CodeUtils;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.DeviceUtil;
import com.xinhe.cashloan.util.ExceptionUtil;
import com.xinhe.cashloan.util.GetMyKey;
import com.xinhe.cashloan.util.SPUtils;
import com.xinhe.cashloan.view.MyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_iv1)
    ImageView loginIv1;
    @Bind(R.id.login_et1)
    EditText loginEt1;
    @Bind(R.id.login_iv11)
    ImageView loginIv11;
    @Bind(R.id.login_iv2)
    ImageView loginIv2;
    @Bind(R.id.login_et2)
    EditText loginEt2;
    @Bind(R.id.login_tv2)
    TextView loginTv2;
    @Bind(R.id.login_line)
    View loginLine;
    @Bind(R.id.login_iv12)
    ImageView loginIv12;
    @Bind(R.id.login_rl2)
    RelativeLayout loginRl2;
    @Bind(R.id.login_v)
    View loginV;
    @Bind(R.id.login_btn)
    Button loginBtn;
    @Bind(R.id.login_ll)
    LinearLayout loginLl;
    @Bind(R.id.login_iv)
    ImageView loginIv;
    @Bind(R.id.login_et_yanzheng)
    EditText loginEtYanzheng;
    @Bind(R.id.login_iv1_yanzheng)
    ImageView loginIv1Yanzheng;
    @Bind(R.id.login_rl_yanzheng)
    RelativeLayout loginRlYanzheng;
    @Bind(R.id.login_v_yanzheng)
    View loginVYanzheng;
    private String phone;
    private TimeCount time;
    private String sysId;
    private String getCode;
    private String savePhone;
    private String code;
    private long mLastBackTime;
    private boolean isVerify = false;
    private CodeUtils codeUtils;
    private String yanZhengCode;
    private String etYanZhengCode;
    private String yanZhengResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        try {
            //设置控件
            setViews();
            //设置监听
            setListener();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setListener() {
        loginEt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    loginIv11.setVisibility(View.GONE);
                } else {
                    loginIv11.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loginEt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    loginIv12.setVisibility(View.GONE);
                } else {
                    loginIv12.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setViews() {
        // 构造CountDownTimer对象
        time = new TimeCount(60000, 1000);
        initYanzheng();
    }

    @OnClick({R.id.login_iv11, R.id.login_iv12, R.id.login_btn, R.id.login_tv2, R.id.login_iv1_yanzheng})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_iv11:
                loginEt1.setText("");
                loginIv11.setVisibility(View.GONE);
                break;
            case R.id.login_iv12:
                loginEt2.setText("");
                loginIv12.setVisibility(View.GONE);
                break;
            case R.id.login_btn:
                if (TextUtils.isEmpty(getCode)) {
                    sendCode();
                } else {
                    login();
                }
                break;
            case R.id.login_tv2:
                sendCode();
                break;
            case R.id.login_iv1_yanzheng:
                initYanzheng();
                break;
        }
    }

    /**
     * 新用户登录
     */
    private void login() {
        if (DeviceUtil.IsNetWork(this) == false) {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        phone = loginEt1.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(getCode)) {
            Toast.makeText(this, "请获取手机验证码", Toast.LENGTH_LONG).show();
            return;
        }

        code = loginEt2.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_LONG).show();
            return;
        }
        if (!code.equals(getCode)) {
            Toast.makeText(this, "验证码输入错误", Toast.LENGTH_LONG).show();
            return;
        }
        if (!phone.equals(savePhone)) {
            Toast.makeText(this, "手机号与验证码不匹配", Toast.LENGTH_LONG).show();
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

        final MyProgressDialog dialog = new MyProgressDialog(this, "登录中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                setResult(-1);
                                finish();

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
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
        phone = loginEt1.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (!CheckUtil.isMobile(phone)) {
            Toast.makeText(this, "手机号输入错误", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(yanZhengResult)) {
            initYanzheng();
            loginRlYanzheng.setVisibility(View.VISIBLE);
            loginVYanzheng.setVisibility(View.VISIBLE);
//            showVerifyDialog();
            return;
        }
        etYanZhengCode=loginEtYanzheng.getText().toString().trim();

        if (TextUtils.isEmpty(etYanZhengCode)){
            Toast.makeText(this, "请输入图片里的结果", Toast.LENGTH_LONG).show();
            return;
        }

        if (!yanZhengResult.equals(etYanZhengCode)){
            Toast.makeText(this, "图片结果输入有误", Toast.LENGTH_LONG).show();
            initYanzheng();
            return;
        }

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

        final MyProgressDialog dialog = new MyProgressDialog(this, "登录中...", R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String URL = Constants.URL;
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
                                loginRl2.setVisibility(View.VISIBLE);
                                loginV.setVisibility(View.VISIBLE);
                                // 开始计时
                                time.start();
                                Toast.makeText(LoginActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    } else if (!TextUtils.isEmpty(result) && result.startsWith("1,")) {
                        MyApplication.userId = result.substring(2);
                        SPUtils.put(LoginActivity.this, "userId", MyApplication.userId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                setResult(-1);
                                finish();
                                overridePendingTransition(R.anim.login_in, R.anim.login_out);

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
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
        loginIv1Yanzheng.setImageBitmap(bitmap);
        yanZhengCode=codeUtils.getCode();
        yanZhengResult=codeUtils.getResult()+"";
    }

    /*
     *
     *
     * 弹出对话框的步骤 1.创建alertDialog的builder. 2.要给builder设置属行, 对话框的内容,样式,按钮
     * 3.通过builder 创建个对话框 4.对话框show()出来
     */
    protected void showVerifyDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.CustomDialog).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.verify);
        final SwipeCaptchaView sc = (SwipeCaptchaView) window.findViewById(R.id.swipeCaptchaView);
        final SeekBar seekBar = (SeekBar) window.findViewById(R.id.dragBar);
        sc.setOnCaptchaMatchCallback(new SwipeCaptchaView.OnCaptchaMatchCallback() {
            @Override
            public void matchSuccess(SwipeCaptchaView swipeCaptchaView) {
                alertDialog.dismiss();
                isVerify = true;
                sendCode();
//                Toast.makeText(LoginActivity.this, "恭喜你啊 验证成功 可以搞事情了", Toast.LENGTH_SHORT).show();
                seekBar.setEnabled(false);
            }

            @Override
            public void matchFailed(SwipeCaptchaView swipeCaptchaView) {
                Toast.makeText(LoginActivity.this, "验证失败了，再试一次吧", Toast.LENGTH_SHORT).show();
                swipeCaptchaView.resetCaptcha();
                seekBar.setProgress(0);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sc.setCurrentSwipeValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //随便放这里是因为控件
                seekBar.setMax(sc.getMaxSwipeValue());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sc.matchCaptcha();
            }
        });
        //测试从网络加载图片是否ok
        Glide.with(this)
                .load(R.mipmap.verify1)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        sc.setImageBitmap(resource);
                        sc.createCaptcha();
                    }
                });
    }

    // 定义一个倒计时的内部类
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            loginTv2.setText("重新发送");
            loginTv2.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            loginTv2.setClickable(false);
            loginTv2.setText(millisUntilFinished / 1000 + "秒");
        }
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

}
