package com.kdx.kdxutils;

import java.io.File;

/**
 * Author  :qujuncai
 * DATE    :18/11/26
 * Email   :qjchzq@163.com
 */
public class KdxFileUtil {
    private static final String kdx_rootDir = "/mnt/sdcard/vm/";
    private static final String sdcard_Dir = "/mnt/sdcard/";
    private static final String kdx_configDir = kdx_rootDir + "config/";
    private static final String kdx_apksDir = kdx_rootDir + "apks/";
    private static final String kdx_logsDir = kdx_rootDir + "logs/";
    private static final String kdx_dataDir = kdx_rootDir + "data/";
    private static final String kdx_resourceDir = kdx_rootDir + "resource/";
    private static final String kdx_logsbakDir = kdx_rootDir + "logsbak/";
    private static final String kdx_libsDir = kdx_rootDir + "libs/";
    private static final String kdx_appsDir = kdx_rootDir + "apps/";
    private static final String kdx_tempDir = kdx_rootDir + "temp/";
    private static final String kdx_updateDir = kdx_rootDir + "update/";
    private static final String kdx_coreDir = kdx_rootDir + "core/";

    public static String getRootDir() {
        File dir = new File(kdx_rootDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_rootDir;
    }

    public static String getConfigDir() {
        File dir = new File(kdx_configDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_configDir;
    }

    public static String getApksDir() {
        File dir = new File(kdx_apksDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_apksDir;
    }

    public static String getLogsDir() {
        File dir = new File(kdx_logsDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_logsDir;
    }

    public static String getDataDir() {
        File dir = new File(kdx_dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_dataDir;
    }

    public static String getResourceDir() {
        File dir = new File(kdx_resourceDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_resourceDir;
    }

    public static String getLogsbakDir() {
        File dir = new File(kdx_logsbakDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_logsbakDir;
    }

    public static String getLibsDir() {
        File dir = new File(kdx_libsDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_libsDir;
    }

    public static String getAppsDir() {
        File dir = new File(kdx_appsDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_appsDir;
    }

    public static String getTempDir() {
        File dir = new File(kdx_tempDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_tempDir;
    }
    public static String getUpdateDir() {
        File dir = new File(kdx_updateDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_updateDir;
    }
    public static String getCoreDir() {
        File dir = new File(kdx_coreDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return kdx_coreDir;
    }
    public static String getSdcardDir() {
        File dir = new File(sdcard_Dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return sdcard_Dir;
    }

}
