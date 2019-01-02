package com.kdx.filemanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kdx.kdxutils.LocalThreadPoolExecutor;


/**
 * Author  :qujuncai
 * DATE    :18/12/29
 * Email   :qjchzq@163.com
 */
public class FileService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocalThreadPoolExecutor.getInstance().submit(new LogManaRunnable());
        return super.onStartCommand(intent, flags, startId);
    }

}
