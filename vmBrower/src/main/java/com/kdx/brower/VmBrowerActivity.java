package com.kdx.brower;


import android.view.*;

import org.apache.logging.log4j.Logger;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;

import com.kdx.kdxutils.config.ActionConfig;
import com.kdx.brower.config.VmBrowerConfig;
import com.kdx.brower.logger.LoggerSetting;
import com.kdx.brower.net.ResultCallback;
import com.kdx.brower.net.VmBoxConnection;
import com.kdx.brower.utils.UIUtil;
import com.kdx.brower.view.MyXWalkActivity;

public class VmBrowerActivity extends MyXWalkActivity {
    private static Logger logger = LoggerSetting.getLogger();
    public static XWalkView xWalkView;
    private int mViewScale = 100;
    private final int WIDTH_PIXELS = 1360;
    private VmBoxConnection vboxConnection;
    public String url = VmBrowerConfig.defaultUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendBootNotice();
        vboxConnection = new VmBoxConnection();
        vboxConnection.setCallBack(new ResultCallbackImp());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(xWalkView!=null){
            xWalkView.resumeTimers();
            xWalkView.onShow();
            xWalkView.load("javascript:(function() { " +
                    "var videos = document.getElementsByTagName('video');" +
                    " for(var i=0;i<videos.length;i++){videos[i].play();}})()",null);
        }
    }

    @Override
    public void onXWalkReady() {
        xWalkView = new XWalkView(this.getApplicationContext(),this);
        setWebView(xWalkView);
        url = VmBrowerConfig.getMainUrl();
        vboxConnection.connectVbox(url);
    }


    /**
     * 发送广播
     */
    private void sendBootNotice() {
        Intent intent = new Intent();
        intent.setAction(ActionConfig.B_LAUNCHER_BOOT);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }
    public class ResultCallbackImp implements ResultCallback {
        @Override
        public void onResult(final String url){

            UIUtil.post(new Runnable() {
                @Override
                public void run() {
                    logger.info("launcher --> launcherActivity onResult"+xWalkView);
                    if (xWalkView != null) {
                        setContentView(xWalkView);
                        xWalkView.load(url, null);
                    }
                }
            });
        }

    }
    private void setWebView(XWalkView webview){
        int widthPixels = getWidthPixels();
        if (widthPixels == 1080 || widthPixels == 1280) {
            mViewScale = 100 * widthPixels / WIDTH_PIXELS;
        }
        XWalkSettings settings = webview.getSettings();
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setUseWideViewPort(true);
        webview.getNavigationHistory().clear();
        webview.clearCache(true);
        //webview.setInitialScale(mViewScale);
        webview.setOnTouchListener(lisenterToch);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.addJavascriptInterface(new NativeMethod(), "NativeMethod");
        webview.setLongClickable(false);
        webview.setOnLongClickListener( new WebView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webview.setResourceClient(new LauncherResourceClient(webview));
        webview.setUIClient(new XWalkUIClient(webview) {
            @Override
            public boolean onConsoleMessage(XWalkView view, String message,
                                            int lineNumber, String sourceId,
                                            ConsoleMessageType messageType) {
                Log.i("webapp", "webapp consol-->Type:" + messageType + " ," + message + " ,source:" + sourceId + ":" + lineNumber);
                return super.onConsoleMessage(view, message, lineNumber, sourceId, messageType);
            }
        });
    }

    class LauncherResourceClient extends XWalkResourceClient{

        public LauncherResourceClient(XWalkView view) {
            super(view);
        }
        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);
            logger.info("launcher --> LauncherResourceClient.onLoadFinished():url = "+url);
        }
        @Override
        public void onReceivedLoadError(XWalkView  view, int errorCode, String description, String failingUrl) {
            super.onReceivedLoadError(view, errorCode, description, failingUrl);
            //Logger.info("launcher --> LauncherResourceClient.onReceivedLoadError():errorCode = "+errorCode);
            final String url = failingUrl;
            final XWalkView webView = view;
            UIUtil.postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.load(url, null);
                }
            }, 5000);
        }
    }
    public int getWidthPixels() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    @Override
    protected void onStop() {
        super.onStop();
        logger.info("launcher --> LauncherAcitivty.onStop()--->"+this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (xWalkView != null) {
            xWalkView.pauseTimers();
            xWalkView.onHide();
        }
        logger.info("launcher --> LauncherAcitivty.onPause()--->"+this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (xWalkView != null) {
            xWalkView.onDestroy();
            xWalkView = null;
        }
        vboxConnection.setCallBack(null);
        logger.info("launcher --> LauncherAcitivty.onDestroy()--->" + this);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    @Override
    public void loadCrosswalk() {
        logger.info("launcher --> VmBrowerActivity.loadCrosswalk():");
//        File file = new File(VmBrowerConfig.SO_FILEPATH);
//        new Thread(new DpRunnable(file)).start();
    }
    boolean isMove = true;
    private long down_time = 0;
    private View.OnTouchListener lisenterToch = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {

            switch(arg1.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    long down_cureent = System.currentTimeMillis();
                    if((down_cureent - down_time) < 300){
                        isMove = false;
                    }else{
                        isMove = true;
                    }
                    down_time = down_cureent;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if(!isMove){
                        return true;
                    }else{
                        break;
                    }
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    return true;
                default:
            }
            return false;
        }
    };



}
