package com.kdx.cmdservice.handler;

import android.text.TextUtils;

import com.kdx.cmdservice.config.Config;
import com.kdx.cmdservice.model.Rep2CMsg;
import com.kdx.cmdservice.model.Req2SMsg;
import com.kdx.cmdservice.net.CmdWebsocketService;
import com.kdx.cmdservice.net.HttpDownload;
import com.kdx.cmdservice.utils.JsonUtil;
import com.kdx.kdxutils.LocalThreadPoolExecutor;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class DownloadHandler extends AbsHandler {
    @Override
    public void handle(Rep2CMsg rep2CMsg) {
        StringBuffer sb = new StringBuffer();
        final String[] paramString = rep2CMsg.getParamString().split(" ");
        if (null != paramString && paramString.length == 2 && !TextUtils.isEmpty(paramString[0])
                && !TextUtils.isEmpty(paramString[1])){
            LocalThreadPoolExecutor.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    HttpDownload.getInstance().downloadRes(paramString[0],paramString[1]);
                }
            });
            sb.append(getMsgType()+" command receive success");
        }else{
            sb.append("command error,example：download http://www.touchfound.net/VmBox.apk /mnt/sdcard/vm/apks/VmBox.apk");
        }
        sendReq2SMsg(rep2CMsg.getSessionId(), sb.toString());
    }

    @Override
    public String getMsgType() {
        return "download";
    }
}
