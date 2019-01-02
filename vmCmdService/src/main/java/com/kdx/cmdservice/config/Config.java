package com.kdx.cmdservice.config;

import android.text.TextUtils;

import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.PropertiesUtil;
import com.kdx.kdxutils.config.GlobalConfig;

/**
 * Author  :qujuncai
 * DATE    :18/12/26
 * Email   :qjchzq@163.com
 */
public class Config {
    private static final String WEBSOCKET_SERVER_URL = "ws://vd.touchfound.net:8181";
    private static final String UPLOAD_URL = "ws://vd.touchfound.net:8181";
    private static String vmID = null;

    public static String getWebsocketServerUrl(){
        String url = WEBSOCKET_SERVER_URL;
        try {
            url = PropertiesUtil.getPropertyValue(KdxFileUtil.getConfigDir()+"cmdService.config","websocketUrl");
        } catch (Exception e) {
            url = WEBSOCKET_SERVER_URL;
        }
        return url;
    }

    public static String getVmId(){
        if(TextUtils.isEmpty(vmID)){
            vmID = GlobalConfig.getVmId();
        }
        return vmID;
    }
}
