package com.kdx.cmdservice.handler;

import android.text.TextUtils;

import com.kdx.cmdservice.model.Rep2CMsg;
import com.kdx.kdxutils.LocalThreadPoolExecutor;
import com.kdx.kdxutils.ZipUtils;


/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class UnzipHandler extends AbsHandler {
    @Override
    public void handle(Rep2CMsg rep2CMsg) {
        final String[] paramString = rep2CMsg.getParamString().split(" ");
        StringBuffer sb = new StringBuffer();
        if (null != paramString && paramString.length == 2 && !TextUtils.isEmpty(paramString[0])
                && !TextUtils.isEmpty(paramString[1])){
            LocalThreadPoolExecutor.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    ZipUtils.unZipFile(paramString[0], paramString[1]);
                }
            });
            sb.append(getMsgType()+" command receive success");
        }else{
            sb.append("command error,example：unzip /mnt/sdcard/vm/webApp.zip /mnt/sdcard/vm/webApp/");
        }
        sendReq2SMsg(rep2CMsg.getSessionId(),sb.toString());
    }

    @Override
    public String getMsgType() {
        return "unzip";
    }
}
