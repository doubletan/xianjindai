package com.xinhe.cashloan.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xinhe.cashloan.R;
import com.xinhe.cashloan.biz.DownLoadManager;
import com.xinhe.cashloan.biz.UpdataInfoParser;
import com.xinhe.cashloan.biz.update.UpdateService;
import com.xinhe.cashloan.entity.UpdataInfo;
import com.xinhe.cashloan.fragment.MainFragment;
import com.xinhe.cashloan.fragment.MeFragment;
import com.xinhe.cashloan.fragment.NewFragment;
import com.xinhe.cashloan.myapp.MyApplication;
import com.xinhe.cashloan.util.Constants;
import com.xinhe.cashloan.util.ExceptionUtil;
import com.xinhe.cashloan.util.SPUtils;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // 版本更新
    private final String TAG = this.getClass().getName();
    private final int UPDATA_NONEED = 10;
    private final int UPDATA_CLIENT = 11;
    private final int GET_UNDATAINFO_ERROR = 12;
    private final int DOWN_ERROR = 13;
    private UpdataInfo info;
    private int localVersion;

    @Bind(R.id.main_activity_fragment_container)
    LinearLayout mainActivityFragmentContainer;
    @Bind(R.id.main_activity_buttom_btn1)
    Button mainActivityButtomBtn1;
    @Bind(R.id.main_activity_buttom_rl1)
    RelativeLayout mainActivityButtomRl1;
    @Bind(R.id.main_activity_buttom_btn2)
    Button mainActivityButtomBtn2;
    @Bind(R.id.main_activity_buttom_rl2)
    RelativeLayout mainActivityButtomRl2;
    @Bind(R.id.main_activity_buttom_btn3)
    Button mainActivityButtomBtn3;
    @Bind(R.id.main_activity_buttom_rl3)
    RelativeLayout mainActivityButtomRl3;
    @Bind(R.id.main_activity_buttom_ll)
    LinearLayout mainActivityButtomLl;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;


    private Button[] btnArray = new Button[3];
    private MainFragment main;
    private NewFragment New;
    private MeFragment me;
    private Fragment[] fragments;
    private long mLastBackTime;
    private int selectedIndex;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            // 4.4版本以上设置 全屏显示，状态栏在界面上方
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                // 透明导航栏
                // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            MyApplication.getApp().addToList(this);
            //检测版本
            testVersion();
            //设置控件
            setViews();
            //设置监听
            setListener();
            if (!SPUtils.contains(this, "userId")) {
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        login();
                    }
                }, 500);

            } else {
                MyApplication.userId = (String) SPUtils.get(this, "userId", "");
                //获取权限
                getPermissions();
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setListener() {

    }

    private void setViews() {

        btnArray[0] = (Button) findViewById(R.id.main_activity_buttom_btn1);
        btnArray[1] = (Button) findViewById(R.id.main_activity_buttom_btn2);
        btnArray[2] = (Button) findViewById(R.id.main_activity_buttom_btn3);
        btnArray[0].setSelected(true);

        main = new MainFragment();
        New = new NewFragment();
        me = new MeFragment();
        fragments = new Fragment[]{New,main, me};
        // 一开始，显示第一个fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_activity_fragment_container, New);
        transaction.show(New );
        transaction.commit();
    }

//    public void initActionBar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
////        setSupportActionBar(toolbar);
////        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setNavigationIcon(R.mipmap.back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // TODO Auto-generated method stub
//        switch (item.getItemId()) {
//            case R.id.menu_about:
//                Toast.makeText(MainActivity.this, "" + "关于", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.menu_settings:
//
//                Toast.makeText(MainActivity.this, "" + "设置", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.menu_quit:
//
//                Toast.makeText(MainActivity.this, "" + "退出", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @OnClick({R.id.main_activity_buttom_rl1, R.id.main_activity_buttom_rl2, R.id.main_activity_buttom_rl3})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.main_activity_buttom_rl1:
                    selectedIndex = 0;
                    break;
                case R.id.main_activity_buttom_rl2:
                    selectedIndex = 1;
                    break;
                case R.id.main_activity_buttom_rl3:
                    selectedIndex = 2;
                    break;
            }
            // 判断单击是不是当前的
            if (selectedIndex != currentIndex) {
                // 不是当前的
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                // 当前hide
                transaction.hide(fragments[currentIndex]);
                // show你选中

                if (!fragments[selectedIndex].isAdded()) {
                    // 以前没添加过
                    transaction.add(R.id.main_activity_fragment_container, fragments[selectedIndex]);
                }
                // 事务
                transaction.show(fragments[selectedIndex]);
                transaction.commit();

                btnArray[currentIndex].setSelected(false);
                btnArray[selectedIndex].setSelected(true);
                currentIndex = selectedIndex;
            }
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            // 对6.0以上的版本设置权限
            // PersonalInformationPermissionsDispatcher.showTakePhotoWithCheck(activity);
            int checkCameraPermission = ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE);
            if (checkCameraPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        Constants.PERMISSION_READ_PHONE_STATE);
            }else {
                    int checkCameraPermission1 = ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (checkCameraPermission1 != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
                    }
            }
        }


    }


    public void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent,Constants.MAINACTIVITY_TO_LOGINACTIVITY);
        overridePendingTransition(R.anim.login_in, R.anim.login_out);
//        LoginDialog loginDialog = new LoginDialog(this,R.style.add_dialog);//创建Dialog并设置样式主题
//        Window win = loginDialog.getWindow();
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//        params.x = -80;//设置x坐标
//        params.y = -60;//设置y坐标
//        win.setAttributes(params);
//        loginDialog.setCanceledOnTouchOutside(false);//设置点击Dialog外部任意区域关闭Dialog
//        loginDialog.show();
//        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        // 下边三行代码可以让对话框里的输入框弹出软键盘
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.login_tips, null);
//        alertDialog.setView(layout);
//
//        alertDialog.setCancelable(true);
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();
//        Window window = alertDialog.getWindow();
//        window.setContentView(R.layout.login_tips);
////        final EditText et = (EditText) window.findViewById(R.id.integral_exchange_tips_et);
////        ImageView iv1 = (ImageView) window.findViewById(R.id.integral_exchange_tips_iv1);
////        ImageView iv2 = (ImageView) window.findViewById(R.id.integral_exchange_tips_iv2);
////        ImageView iv3 = (ImageView) window.findViewById(R.id.integral_exchange_tips_iv3);
////        ImageView iv4 = (ImageView) window.findViewById(R.id.integral_exchange_tips_iv4);
////        ImageView iv5 = (ImageView) window.findViewById(R.id.integral_exchange_tips_iv5);
////        ImageView iv6 = (ImageView) window.findViewById(R.id.integral_exchange_tips_iv6);


    }


    private void testVersion() {
        try {
            localVersion = getVersionCode();
            CheckVersionTask cv = new CheckVersionTask();
            new Thread(cv).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getVersionCode() throws Exception {
        // getPackageName()是你当前类的包名0代表是获取版本信
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionCode;
    }

    public class CheckVersionTask implements Runnable {
        InputStream is;

        public void run() {
            try {
                String path = Constants.versionUrl;
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // 从服务器获得个输入流
                    is = conn.getInputStream();
                } else {
                    return;
                }
                info = UpdataInfoParser.getUpdataInfo(is);
                String a = info.getVersion();
                int versionCode = Integer.valueOf(a);
                if (versionCode <= localVersion) {
                    // 版本号相
                    Message msg = new Message();
                    msg.what = UPDATA_NONEED;
                    handler.sendMessage(msg);
                } else {
                    // 版本号不相同
                    Message msg = new Message();
                    msg.what = UPDATA_CLIENT;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                handler.sendMessage(msg);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_NONEED:
                    // 不需要更
                    break;
                case UPDATA_CLIENT:
                    // 对话框知用户升级程序
                    showUpdataDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    // 服务器超时
                    // Toast.makeText(getApplicationContext(), "获取服务器更新信息失败",
                    // 1).show();
                    break;
                case DOWN_ERROR:
                    // 下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /*
     *
     * 弹出对话框得知用户更新程序
     *
     * 弹出对话框的步骤 1.创建alertDialog的builder. 2.要给builder设置属行, 对话框的内容,样式,按钮
     * 3.通过builder 创建个对话框 4.对话框show()出来
     */
    protected void showUpdataDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.CustomDialog).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.version_dialog);
        TextView tv1 = (TextView) window.findViewById(R.id.version_dialog_tv);
        tv1.setText(info.getDescription());
        RelativeLayout rl3 = (RelativeLayout) window.findViewById(R.id.version_dialog_rl2);
        TextView tv = (TextView) window.findViewById(R.id.version_dialog_tv2);
        tv.setText("更新");
        rl3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UpdateService.Builder.create(info.getUrl()).build(MainActivity.this);
                alertDialog.dismiss();
            }
        });
//		AlertDialog.Builder builer = new Builder(this);
//		builer.setTitle("版本升级");
//		builer.setMessage(info.getDescription());
//		builer.setCancelable(false);
//		// 当点确定按钮时从服务器上下载 新的apk 然后安装
//		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				// 下载apk,更新
//				downLoadApk();
//			}
//		});
//		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				// do sth
//			}
//		});
//		AlertDialog dialog = builer.create();
//		dialog.show();
    }

    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = DownLoadManager.getFileFromServer(info.getUrl(), pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); // 结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        // 执行的数据类
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
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
        // finish while click back key 2 times during 1s.
        if ((System.currentTimeMillis() - mLastBackTime) < 2000) {
            finish();
            MyApplication.getApp().onTerminate();
        } else {
            mLastBackTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 102:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "获取手机状态权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            case 103:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "手机储存权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 104:
                if (-1==resultCode){
                    getPermissions();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
