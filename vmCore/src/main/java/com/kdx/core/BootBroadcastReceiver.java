package com.kdx.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kdx.core.logger.LoggerSetting;
import com.kdx.kdxutils.config.ActionConfig;

/**
 * Author  :qujuncai
 * DATE    :18/12/14
 * Email   :qjchzq@163.com
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static org.apache.logging.log4j.Logger logger = LoggerSetting.getLogger();
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        logger.info("CoreBroadcastReceiver receive action={}",action);
        if (action.equals(ActionConfig.B_BOOT_COMPLETED)) {
            logger.info("system start");
        } else if (action.equals(ActionConfig.B_ACTION_SHUTDOWN)) {
            logger.info("system shutdown");
        } else if(action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_DATE_CHANGED)){
        }
    }
}
