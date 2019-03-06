package com.kdx.core.config;

import android.text.TextUtils;
import android.util.Log;

import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.PropertiesUtil;
import com.kdx.kdxutils.config.ActionConfig;
import com.kdx.kdxutils.config.GlobalConfig;

/**
 * Author  :qujuncai
 * DATE    :18/12/12
 * Email   :qjchzq@163.com
 */
public class CoreConfig {
    private static String IP_DEBUG = "kcs.touchfound.net";//测试环境
    private static final String INITQR = "http://vms.touchfound.net/mp/activate/reg?uniqueCode=";
    private static final String CORECONFIG_PROPERTIES = KdxFileUtil.getConfigDir()+"core/coreConfig.properties";
    private static final String SOCKET_URL = "kcs.touchfound.net";
    private static final int SOCKET_PORT = 8031;

    public static final String vmImagePath="/mnt/sdcard/vmImage/vmImage.zip";

    private static final String KEY_SOCKET_URL = "socketUrl";
    private static final String KEY_SOCKET_PORT = "socketPort";
    private static final String KEY_OTHERSET_ACTION = "otherset";
    private static final String KEY_LANCHER_ACTION = "launcher";
    private static final String KEY_INITQR = "initqr";


    private static String initQrContent = CoreConfig.INITQR;
    private static String socketUrl = CoreConfig.SOCKET_URL;
    private static int socketPort = CoreConfig.SOCKET_PORT;
    private static String otherSetAction = ActionConfig.A_ACTION_OTHERSET;
    private static String launcherAction = ActionConfig.A_LAUNCHER_ACTION;
    public static void initData(){
        try {
            String config_qr = PropertiesUtil.getPropertyValue(GlobalConfig.VM_UPAN_PATH+"config/core/coreConfig.properties", CoreConfig.KEY_INITQR);
            if(!TextUtils.isEmpty(config_qr)){
                initQrContent = config_qr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**------------------------------------*/
        try {
            String config_socketurl  = PropertiesUtil.getPropertyValue(GlobalConfig.VM_UPAN_PATH+"config/core/coreConfig.properties",CoreConfig.KEY_SOCKET_URL);
            if(!TextUtils.isEmpty(config_socketurl)){
                socketUrl = config_socketurl;
            }
        } catch (Exception e) {
        }
        //----------------------------------
        try {
            String str_socketPort = PropertiesUtil.getPropertyValue(GlobalConfig.VM_UPAN_PATH+"config/core/coreConfig.properties",CoreConfig.KEY_SOCKET_PORT);
            socketPort = Integer.valueOf(str_socketPort);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        /**----------------------------------*/
        try {
            Log.i("core","getOthersetAction start");
            String config_otherset = PropertiesUtil.getPropertyValue(CoreConfig.CORECONFIG_PROPERTIES,CoreConfig.KEY_OTHERSET_ACTION);
            if(!TextUtils.isEmpty(config_otherset)){
                otherSetAction = config_otherset;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        /**-------------------------*/
        try {
            String config_value = PropertiesUtil.getPropertyValue(CoreConfig.CORECONFIG_PROPERTIES,CoreConfig.KEY_LANCHER_ACTION);
            if(!TextUtils.isEmpty(config_value)){
                launcherAction = config_value;
            }
        } catch (Exception e) {
        }
    }
    public static String getInitUrl(){
        return initQrContent;
    }
    public static String getSocketUrl(){
        return socketUrl;
    }
    public static int getSocketPort(){
        return socketPort;
    }

    public static String getOthersetAction(){
        return otherSetAction;
    }
    public static String getLauncherAction(){
        return launcherAction;
    }
}
