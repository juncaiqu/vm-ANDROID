package com.kdx.core.config;

import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.PropertiesUtil;
import com.kdx.kdxutils.config.ActionConfig;

/**
 * Author  :qujuncai
 * DATE    :18/12/12
 * Email   :qjchzq@163.com
 */
public class CoreConfig {
    private static String IP_RELEASE = "kcs.touchfound.com";//正式环境
    private static String IP_DEBUG = "kcs.touchfound.net";//测试环境
    private static final String INITURL = "http://vms.touchfound.net/mp/activate/reg?uniqueCode=";
    private static final String KEY_INITURL = "initUrl";
    private static final String CORECONFIG_PROPERTIES = KdxFileUtil.getConfigDir()+"core/coreConfig.properties";
    private static final String SOCKET_URL = IP_DEBUG;
    private static final String KEY_SOCKET_URL = "socketUrl";
    private static final int SOCKET_PORT = 8031;
    private static final String KEY_SOCKET_PORT = "socketPort";
    private static final String KEY_OTHERSET_ACTION = "otherset";
    private static final String KEY_LANCHER_ACTION = "launcher";

    public static String getInitUrl(){
        String url = CoreConfig.INITURL;
        try {
            url = PropertiesUtil.getPropertyValue(CoreConfig.CORECONFIG_PROPERTIES,CoreConfig.KEY_INITURL);
        } catch (Exception e) {
           // e.printStackTrace();
        }
        return url;
    }
    public static String getSocketUrl(){
        String socket = CoreConfig.SOCKET_URL;
        try {
            socket = PropertiesUtil.getPropertyValue(CoreConfig.CORECONFIG_PROPERTIES,CoreConfig.KEY_SOCKET_URL);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return socket;
    }
    public static int getSocketPort(){
        int socketPort = CoreConfig.SOCKET_PORT;
        try {
            String str_socketPort = PropertiesUtil.getPropertyValue(CoreConfig.CORECONFIG_PROPERTIES,CoreConfig.KEY_SOCKET_PORT);
            socketPort = Integer.valueOf(str_socketPort);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return socketPort;
    }

    public static String getOthersetAction(){
        String otherset = ActionConfig.A_ACTION_OTHERSET;
        try {
            otherset = PropertiesUtil.getPropertyValue(CoreConfig.CORECONFIG_PROPERTIES,CoreConfig.KEY_OTHERSET_ACTION);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return otherset;
    }
    public static String getLauncherAction(){
        String launcherAction = ActionConfig.A_LAUNCHER_ACTION;
        try {
            launcherAction = PropertiesUtil.getPropertyValue(CoreConfig.CORECONFIG_PROPERTIES,CoreConfig.KEY_LANCHER_ACTION);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return launcherAction;
    }
}
