package com.kdx.cmdservice.handler;

import android.content.Intent;
import android.text.TextUtils;

import com.kdx.cmdservice.CmdApplication;
import com.kdx.cmdservice.model.Rep2CMsg;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class UploadHandler extends AbsHandler {
    @Override
    public void handle(Rep2CMsg rep2CMsg) {
        String[] paramString = rep2CMsg.getParamString().split(" ");
        StringBuffer sb = new StringBuffer();
        if (null != paramString && paramString.length == 1 && !TextUtils.isEmpty(paramString[0])){
            startUploadBroadcast(paramString[0]);
            sb.append(getMsgType()+" command receive success");
        }else{
            sb.append("command error,example：command error,example：upload /mnt/sdcard/vm/apks/VmBox.apk");
        }
        sendReq2SMsg(rep2CMsg.getSessionId(), sb.toString());
    }
    private void startUploadBroadcast(String path) {
        Intent intent = new Intent("com.kdx.receive.VmBoxUploadReceive");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("localPath", path);
        CmdApplication.getInstance().sendBroadcast(intent);

    }
    @Override
    public String getMsgType() {
        return "upload";
    }
}
