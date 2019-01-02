package com.kdx.brower.utils;

import android.os.Handler;
import android.os.Looper;

import com.kdx.brower.BaseApplication;

/**
 * Created by qujc-pc on 2015/7/24.
 */
public class UIUtil {
    /** 获取主线程的handler */
    public static Handler getHandler() {
        //获得主线程的looper
        Looper mainLooper = BaseApplication.getMainThreadLooper();
        //获取主线程的handler
        Handler handler = new Handler(mainLooper);
        return handler;
    }


    /** 在主线程执行runnable */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }
    public static boolean postDelayed(Runnable runnable,long time) {
        return getHandler().postDelayed(runnable,time);
    }

    /** 从主线程looper里面移除runnable */
    public static void removeCallbacks(Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }
}
