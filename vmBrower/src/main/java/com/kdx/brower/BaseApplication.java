package com.kdx.brower;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.kdx.brower.exception.MyExceptionHandler;
import com.kdx.brower.logger.LoggerSetting;

import org.apache.logging.log4j.Logger;

/**
 * Created by qujc-pc on 2015/7/24.
 */
public class BaseApplication extends Application {
    private static Logger logger = LoggerSetting.getLogger();
    private static Handler mMainThreadHandler;
    private static Looper mMainLooper;
    private static BaseApplication mInstance;
    @Override
    public void onCreate() {
    	super.onCreate();
        mMainThreadHandler = new Handler();
        mMainLooper = getMainLooper();
        mInstance = this;
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
       
    }
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    public static BaseApplication getApplication() {
        return mInstance;
    }
}
