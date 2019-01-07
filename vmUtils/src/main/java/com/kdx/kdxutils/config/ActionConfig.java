package com.kdx.kdxutils.config;

/**
 * Author  :qujuncai
 * DATE    :18/12/6
 * Email   :qjchzq@163.com
 */
public class ActionConfig {
    /**
     * launcher ACTIVITY 启动
     */
    public static final String A_LAUNCHER_ACTION = "com.kdx.launcher";
    /**
     * launcher启动广播
     */
    public static final String B_LAUNCHER_BOOT = "com.kdx.launcher.BOOT";
    /**
     * launcher启动广播
     */
    public static final String B_LAUNCHER_STOP = "com.kdx.launcher.STOP";
    /**
     * 维护模式切换广播
     */
    public static final String B_MODE_SWITCH = "com.kdx.MODE_SWITCH";
    /**
     * 看门狗广播
     */
    public static final String B_WATCHDOG_FEED = "com.kdx.watchdog.feed";
    /**
     * 看门狗广播
     */
    public static final String B_KDX_UPDATE = "com.kdx.update";
    /**
     * U盘拔出广播
     */
    public static final String B_MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
    /**
     * U盘插入广播
     */
    public static final String B_MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";

    public static final String B_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String B_ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
    /**
     * 重启广播
     */
    public static final String B_REBOOT = "com.kdx.reboot";

    /**
     * install 服务
     */
    public static final String S_ACTION_INSTALL = "com.kdx.install.INSTALL_SERVICE";


    /**
     * 其它设置activity
     */
    public static final String A_ACTION_OTHERSET = "com.kdx.otherset";


}
