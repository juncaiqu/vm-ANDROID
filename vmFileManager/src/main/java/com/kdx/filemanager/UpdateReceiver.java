package com.kdx.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.kdx.kdxutils.config.ActionConfig;

/**
 * Author  :qujuncai
 * DATE    :18/12/29
 * Email   :qjchzq@163.com
 */
public class UpdateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(TextUtils.equals(ActionConfig.B_KDX_UPDATE,intent.getAction())){
            Intent fileService = new Intent(context, FileService.class);
            context.startService(fileService);
        }
    }


}
