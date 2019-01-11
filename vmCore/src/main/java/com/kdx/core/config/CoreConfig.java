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
    private static final String INITQR = "http://vms.touchfound.com/mp/activate/reg?uniqueCode=";

    private static final String CORECONFIG_PROPERTIES = KdxFileUtil.getConfigDir()+"core/coreConfig.properties";
    private static final String SOCKET_URL = "kcs.touchfound.com";
    private static final int SOCKET_PORT = 8031;

    private static final String KEY_SOCKET_URL = "socketUrl";
    private static final String KEY_SOCKET_PORT = "socketPort";
    private static final String KEY_OTHERSET_ACTION = "otherset";
    private static final String KEY_LANCHER_ACTION = "launcher";
    private static final String KEY_INITQR = "initqr";

    public static String getInitUrl(){
        String url = CoreConfig.INITQR;
        try {

            String config_value = PropertiesUtil.getPropertyValue(GlobalConfig.VM_UPAN_PATH+"config/core/coreConfig.properties",CoreConfig.KEY_INITQR);
            if(!TextUtils.isEmpty(config_value)){
                url = config_value;
            }
        } catch (Exception e) {
           // e.printStackTrace();
        }
        return url;
    }
    public static String getSocketUrl(){
        String socket = CoreConfig.SOCKET_URL;
        try {
            String config_value  = PropertiesUtil.getPropertyValue(GlobalConfig.VM_UPAN_PATH+"config/core/coreConfig.properties",CoreConfig.KEY_SOCKET_URL);
            if(!TextUtils.isEmpty(config_value)){
                socket = config_value;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return socket;
    }
    public static int getSocketPort(){
        int socketPort = CoreConfig.SOCKET_PORT;
        try {
            String str_socketPort = PropertiesUtil.getPropertyValue(GlobalConfig.VM_UPAN_PATH+"config/core/coreConfig.properties",CoreConfig.KEY_SOCKET_PORT);
            socketPort = Integer.valueOf(str_socketPort);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return socketPort;
    }

    public static String getOthersetAction(){
        String otherset = ActionConfig.A_ACTION_OTHERSET;
        try {
            Log.i("core","getOthersetAction start");
            String config_value = PropertiesUtil.getPropertyValue(CoreConfig.CORECONFIG_PROPERTIES,CoreConfig.KEY_OTHERSET_ACTION);
            if(!TextUtils.isEmpty(config_value)){
                otherset = config_value;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        Log.i("core","getOthersetAction end");
        return otherset;
    }
    public static String getLauncherAction(){
        String launcherAction = ActionConfig.A_LAUNCHER_ACTION;
        try {
            Log.i("core","getLauncherAction start");
            String config_value = PropertiesUtil.getPropertyValue(CoreConfig.CORECONFIG_PROPERTIES,CoreConfig.KEY_LANCHER_ACTION);
            if(!TextUtils.isEmpty(config_value)){
                launcherAction = config_value;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        Log.i("core","getLauncherAction end");
        return launcherAction;
    }
}
