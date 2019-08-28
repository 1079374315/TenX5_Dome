package com.gsls.pjh_exclusive;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView tenX5_webView;
    private WebSettings webSettings;
    private final static String url = "http://www.google.cn/maps/@25.0620047,110.5921573,15109095m/data=!3m1!1e3";
    private static final String TAG = "MainActivity";

    /**
     * 激活 js 调用，设置 webView 活跃状态
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onResume() {
        super.onResume();
        tenX5_webView.onResume();
        tenX5_webView.getSettings().setJavaScriptEnabled(true);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tenX5_webView = findViewById(R.id.tenX5_webView);
        webSettings = tenX5_webView.getSettings();

        initWebView();//初始化腾讯 X5 webView
        loadWe();//腾讯 X5 加载网页

        if(tenX5_webView != null){
            tenX5_webView.loadUrl(url);//加载网页
        }

    }


    /**
     * 初始化腾讯 X5 webView
     */
    private void initWebView() {

        //优化异常上报
        com.tencent.smtt.sdk.WebView.getCrashExtraMessage(this);
        //视频为了避免闪屏和透明问题
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //避免输入法界面弹出后遮挡输入光标的问题
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //非wifi情况下，主动下载x5内核
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.i(TAG,"腾讯 X 5 初始化:" + arg0);
                Log.i(TAG,arg0 == true ? "腾讯 X5 初始化成功！" : "腾讯 X5 初始化失败！");
            }
            @Override
            public void onCoreInitFinished() {

            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);

    }


    /**
     * 腾讯 X5 加载网页
     */
    private void loadWe(){

        if(webSettings != null){
            webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
            webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
            webSettings.setDisplayZoomControls(true); //隐藏原生的缩放控件
            webSettings.setBlockNetworkImage(false);//解决图片不显示
            webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
            webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        }

        if(tenX5_webView != null){

            //该界面打开更多链接
            tenX5_webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                    webView.loadUrl(s);
                    return true;
                }
            });

            //监听网页的加载进度
            tenX5_webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView webView, int i) {
                    Log.i(TAG,"当前 腾讯X5 网速:" + i);
                    //当前网页加载到 100% 后就显示 腾讯X5 的webView组件
                    if (i < 100) {
                        webView.setVisibility(View.GONE);
                    } else {
                        webView.setVisibility(View.VISIBLE);
                    }

                }
            });

        }

    }


    /**
     * 退出界面暂停 webView的活跃
     */
    @Override
    protected void onPause() {
        super.onPause();
        tenX5_webView.onPause();
        tenX5_webView.getSettings().setLightTouchEnabled(false);
    }


    /**
     * 销毁 放置内存泄漏
     */
    @Override
    protected void onDestroy() {
        if (this.tenX5_webView != null) {
            tenX5_webView.destroy();
        }
        super.onDestroy();
    }

}
