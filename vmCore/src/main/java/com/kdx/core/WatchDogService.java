package com.kdx.core;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.kdx.core.logger.LoggerSetting;
import com.kdx.core.utils.ContextUtil;
import com.kdx.kdxutils.config.ActionConfig;

import org.apache.logging.log4j.Logger;

public class WatchDogService extends Service {
    private static Logger logger = LoggerSetting.getLogger();

    private Handler handler;
    private HandlerThread handlerTask;
    private TimerTask task_watchDog;
    private Timer timer = new Timer();
    private int step = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handlerTask = new HandlerThread("handlerTask");
        handlerTask.setPriority(Thread.MIN_PRIORITY);
        handlerTask.start();
        handler = new Handler(handlerTask.getLooper());
        task_watchDog = new TimerTask() {
            @Override
            public void run() {
                logger.info("send B_WATCHDOG_FEED");
                ContextUtil.sendBroadCast(getApplicationContext(), ActionConfig.B_WATCHDOG_FEED);
                if ((step++ % 120) == 0) {
                    logger.info("send update");
                    ContextUtil.sendBroadCast(getApplicationContext(), ActionConfig.B_KDX_UPDATE);
                }
            }
        };
        timer.schedule(task_watchDog, 0, 60 * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
