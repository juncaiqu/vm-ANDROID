package com.kdx.cmdservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kdx.cmdservice.exception.MyExceptionHandler;
import com.kdx.cmdservice.logger.LoggerSetting;
import com.kdx.kdxutils.LocalThreadPoolExecutor;

import org.apache.logging.log4j.Logger;

/**
 * Author  :qujuncai
 * DATE    :18/12/18
 * Email   :qjchzq@163.com
 */
public class VmService extends Service {
    private static Logger logger = LoggerSetting.getLogger();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logger.info("VmService.onCreate");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            builder = new Notification.Builder(this).setTicker("").setSmallIcon(R.drawable.ic_launcher).getNotification();
            startForeground(1, builder);
        }

        LocalThreadPoolExecutor.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                logger.info(" CmdMain.getSingleton().start");
                CmdMain.getSingleton().start(getApplicationContext());
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
    }


    @Override
    public void onDestroy() {
        logger.info("VmService.onDestroy");
        CmdMain.getSingleton().close();
        super.onDestroy();

    }
}
