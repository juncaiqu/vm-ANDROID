package com.kdx.core;

import android.app.Application;
import com.kdx.core.config.CoreConfig;
import com.kdx.core.exception.CoreExceptionHandler;

/**
 * Created by qujc-pc on 2015/7/24.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
    	super.onCreate();
        CoreConfig.initData();
        Thread.setDefaultUncaughtExceptionHandler(new CoreExceptionHandler());
       
    }
}
