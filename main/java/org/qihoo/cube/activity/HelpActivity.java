package org.qihoo.cube.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;

import org.qihoo.cube.R;

/**
 * Created by wangjinfa on 2015/9/7.
 */
public class HelpActivity extends Activity {

    private WebView helpView;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initViews();
        adjust();
    }

    public void initViews() {
        helpView = (WebView)findViewById(R.id.web_view);
        helpView.loadUrl("file:///android_asset/document.html");
    }

    public void adjust() {
        webSettings = helpView.getSettings();
        webSettings.setJavaScriptEnabled(true);



        // User settings

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放



        webSettings.setLoadWithOverviewMode(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;

//        if (mDensity == 240) {
//            webSettings.setDefaultZoom(ZoomDensity.FAR);
//        } else if (mDensity == 160) {
//            webSettings.setDefaultZoom(ZoomDensity.MEDIUM);
//        } else if(mDensity == 120) {
//            webSettings.setDefaultZoom(ZoomDensity.CLOSE);
//        }else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
//            webSettings.setDefaultZoom(ZoomDensity.FAR);
//        }else if (mDensity == DisplayMetrics.DENSITY_TV){
//            webSettings.setDefaultZoom(ZoomDensity.FAR);
//        }else{
//            webSettings.setDefaultZoom(ZoomDensity.MEDIUM);
//        }
        webSettings.setDefaultZoom(ZoomDensity.FAR);


        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
         * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
         */
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
    }
}
