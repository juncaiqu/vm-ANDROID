package com.kdx.cmdservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.kdx.cmdservice.logger.LoggerSetting;
import com.kdx.kdxutils.config.ActionConfig;

import org.apache.logging.log4j.Logger;

/**
 * Author  :qujuncai
 * DATE    :18/12/18
 * Email   :qjchzq@163.com
 */
public class CmdBroadcastReceiver extends BroadcastReceiver{
private static Logger logger = LoggerSetting.getLogger();

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.info("CmdBroadcastReceiver:{}",intent.getAction());
        if(TextUtils.equals(intent.getAction(), ActionConfig.B_LAUNCHER_BOOT)){
            Intent vmService = new Intent(context, VmService.class);
            context.startService(vmService);
        }else if(TextUtils.equals(intent.getAction(), ActionConfig.B_LAUNCHER_STOP)){
            Intent vmService = new Intent(context, VmService.class);
            context.stopService(vmService);
        }
    }
}
