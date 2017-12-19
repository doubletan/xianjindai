package com.xinhe.cashloan.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.biz.update.UpdateService;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.DeviceUtil;
import com.xinhe.cashloan.util.ExceptionUtil;
import com.xinhe.cashloan.util.GetPathFromUri4kitkat;
import com.xinhe.cashloan.util.PopupWindowUtil;
import com.xinhe.cashloan.util.Utill;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WebViewTitleActivity extends AppCompatActivity {

    private WebView myWebView;
    private ProgressBar bar;
    private String myUrl;

    private ValueCallback mFilePathCallback;
    private File vFile;
    private Uri origUri;
    private TextView titleTv;
    private Toolbar toor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_title);
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
                LinearLayout barLl = (LinearLayout) findViewById(R.id.web_top);
                barLl.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) barLl.getLayoutParams();
                ll.height = Utill.getStatusBarHeight(this);
                ll.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                barLl.setLayoutParams(ll);
            }
            initActionBar();
            // 初始化控件
            initViews();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
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

    private void initViews() {
        myWebView = (WebView) findViewById(R.id.my_web);
        titleTv = (TextView) findViewById(R.id.my_toolbar_tv);
        toor = (Toolbar) findViewById(R.id.my_toolbar);
        bar = (ProgressBar) findViewById(R.id.common_web_bar);
        Intent intent = getIntent();
        myUrl = intent.getStringExtra("url");


        // 设置WebView属性，能够执行Javascript脚本
        myWebView.getSettings().setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞

        // 有些网页webview不能加载
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        myWebView.getSettings().setSupportZoom(true);// 是否可以缩放，默认true
        myWebView.getSettings().setBuiltInZoomControls(false);// 是否显示缩放按钮，默认false
        myWebView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        myWebView.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        myWebView.getSettings().setAppCacheEnabled(true);// 是否使用缓存
        myWebView.getSettings().setDomStorageEnabled(true);// DOM Storage
//		myWebView.getSettings().setUseWideViewPort(true);
//		myWebView.getSettings().setDatabaseEnabled(true);
//		myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//		myWebView.getSettings().setBlockNetworkImage(true);
//		myWebView.getSettings().setAllowFileAccess(true);
//		myWebView.getSettings().setSaveFormData(false);
//		myWebView.getSettings().setLoadsImagesAutomatically(true);
//        myWebView.getSettings().setSupportMultipleWindows(true);
//        myWebView.getSettings().setLoadWithOverviewMode(true);
//        myWebView.getSettings().setUseWideViewPort(true);

        if (Build.VERSION.SDK_INT >= 21) {
            myWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        /**
         * 设置获取位置
         */
        //启用数据库
        myWebView.getSettings().setDatabaseEnabled(true);
        //设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        myWebView.getSettings().setGeolocationDatabasePath(dir);
        //启用地理定位
        myWebView.getSettings().setGeolocationEnabled(true);
        //开启DomStorage缓存
        myWebView.getSettings().setDomStorageEnabled(true);



        myWebView.setWebChromeClient(new WebChromeClient() {
            //加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根

                if (newProgress == 100) {
                    titleTv.setText(view.getTitle());
                    bar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    titleTv.setText("加载中...");
                    bar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    bar.setProgress(newProgress);//设置进度值
                }
            }

            //获取位置
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            /**
             * h5打开相机或相册
             */

            //5.0+
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                try {
                    showMyDialog();
                    mFilePathCallback = filePathCallback;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }


            // Andorid 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                try {
                    showMyDialog();
                    mFilePathCallback = uploadFile;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Andorid 3.0 +
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
                try {
                    showMyDialog();
                    mFilePathCallback = uploadFile;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Android 3.0
            public void openFileChooser(ValueCallback<Uri> uploadFile) {
                try {
                    showMyDialog();
                    mFilePathCallback = uploadFile;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        myWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                if (url.endsWith(".apk")) {//判断是否是.apk结尾的文件路径
                    if (DeviceUtil.isWifiAvailable(WebViewTitleActivity.this)) {
                        UpdateService.Builder.create(url).build(WebViewTitleActivity.this);
                    } else {
                        final AlertDialog alertDialog = new AlertDialog.Builder(WebViewTitleActivity.this,R.style.CustomDialog).create();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                        Window window = alertDialog.getWindow();
                        window.setContentView(R.layout.load_dialog);
                        TextView tv1 = (TextView) window.findViewById(R.id.integral_exchange_tips1_tv);
                        tv1.setText("亲，您现在是非wifi状态下，确定要下载吗？");
                        RelativeLayout rl2 = (RelativeLayout) window.findViewById(R.id.integral_exchange_tips1_rl1);
                        RelativeLayout rl3 = (RelativeLayout) window.findViewById(R.id.integral_exchange_tips1_rl2);
                        rl2.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        rl3.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                UpdateService.Builder.create(url).build(WebViewTitleActivity.this);
                                alertDialog.dismiss();
                            }
                        });
                    }

                }
            }
        });

        myWebView.setWebViewClient(new WebViewTitleActivity.MyWebViewClient());

        if (!DeviceUtil.IsNetWork(this)){
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("网络异常，请检查网络")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    })
                    .show();
        }else{
            myWebView.loadUrl(myUrl);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            WebView.HitTestResult hitTestResult = view.getHitTestResult();
            if (!TextUtils.isEmpty(url) && hitTestResult == null) {
                view.loadUrl(url);
                return true;
            }else if (url.contains("platformapi/startapp")) {
                try {
                    Intent intent;
                    intent = Intent.parseUri(url,
                            Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    // intent.setSelector(null);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(WebViewTitleActivity.this, "请安装最新版支付宝", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (url.contains("weixin://wap/pay?")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(WebViewTitleActivity.this, "请安装最新版微信", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (url.contains("mqqapi://forward/url?")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(WebViewTitleActivity.this, "请安装最新版QQ", Toast.LENGTH_SHORT).show();
                }
                return true;
            }else if (url.contains("tmast://appdetails?")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(WebViewTitleActivity.this, "请安装最新版应用宝", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            else if (!(url.contains("http://") || url.contains("https://"))) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
//            else {
//                view.loadUrl(url);
//            }
            return false;
        }
    }


    private void showMyDialog() {
        View rootView=getLayoutInflater().inflate(R.layout.activity_main,null);
        PopupWindowUtil.showPopupWindow(this,rootView,"相机","文件","取消",
                new PopupWindowUtil.onPupupWindowOnClickListener() {
                    @Override
                    public void onFirstButtonClick() {
                        int flag1 = ActivityCompat.checkSelfPermission(WebViewTitleActivity.this, Manifest.permission.CAMERA);
                        int flag2 = ActivityCompat.checkSelfPermission(WebViewTitleActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (PackageManager.PERMISSION_GRANTED != flag1||PackageManager.PERMISSION_GRANTED != flag2) {
                            ActivityCompat.requestPermissions(WebViewTitleActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_CAMERA_PERMISSION);
                            cancelFilePathCallback();
                        }else {
                            takeForPicture();
                        }
                    }

                    @Override
                    public void onSecondButtonClick() {
                        int flag2 = ActivityCompat.checkSelfPermission(WebViewTitleActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (PackageManager.PERMISSION_GRANTED != flag2) {
                            ActivityCompat.requestPermissions(WebViewTitleActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_READ_EXTERNAL_PERMISSION);
                            cancelFilePathCallback();
                        }else {
                            takeForPhoto();
                        }
                    }

                    @Override
                    public void onCancleButtonClick() {
                        cancelFilePathCallback();
                    }
                });
    }

    /**
     * 调用相册
     */
    private void takeForPhoto() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),Constants.REQUEST_CODE_PICK_PHOTO);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),Constants.REQUEST_CODE_PICK_PHOTO);
        }
    }


    /**
     * 调用相机
     */
    private void takeForPicture() {

        try {
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                vFile = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/xianjindai/");//图片位置
                if (!vFile.exists()) {
                    vFile.mkdirs();
                }
            } else {
                Toast.makeText(WebViewTitleActivity.this, "未挂载sdcard", Toast.LENGTH_LONG).show();
                return;
            }

            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new
                    Date()) + ".jpg";

            Uri uri = Uri.fromFile(new File(vFile, fileName));

            //拍照所存路径
            origUri = uri;

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (Build.VERSION.SDK_INT > 23) {//7.0及以上
//            Uri contentUri = getUriForFile(MainActivity.this, "com.xinhe.crame", picturefile);
//            grantUriPermission(getPackageName(),contentUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
//        } else {//7.0以下
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
//        }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
            startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PICETURE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cancelFilePathCallback() {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    private void takePhotoResult(int resultCode, Intent data) {
        if (mFilePathCallback != null){
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                Uri uri = Uri.fromFile(new File(path));
                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    mFilePathCallback.onReceiveValue(uri);
                }

            }else {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }
    }

    private void takePictureResult(int resultCode) {
        if (mFilePathCallback != null) {
            if (resultCode == RESULT_OK) {

                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{origUri});
                } else {
                    mFilePathCallback.onReceiveValue(origUri);
                }
            } else {
                //点击了file按钮，必须有一个返回值，否则会卡死
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 105:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 105:
                takePictureResult(resultCode);
                break;

            case 106:
                takePhotoResult(resultCode, data);

                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myWebView.destroy();
    }
}
