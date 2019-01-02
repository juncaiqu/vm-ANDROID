package com.kdx.kdxutils.config;

import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.PropertiesUtil;

/**
 * Author  :qujuncai
 * DATE    :18/12/6
 * Email   :qjchzq@163.com
 */
public class GlobalConfig {
    public static final String LOCALCONFIG_PATH = KdxFileUtil.getConfigDir()+"/local.properties";
    public static final String LOCAL_KEY_VMID = "vmId";
    public static final String LOCAL_KEY_ISINSTALL = "isInstall";
    public static final String LOCAL_ISINSTALL_YES = "yes";
    public static final String LOCAL_ISINSTALL_NO = "no";
    public static final String LOCAL_KEY_VMTYPE = "vmType";
    public static final String CONFIG_KEY_IMGTYPE = "imgType";
    public static final String UPAN_PATH = "/mnt/usbdisk_1.1.1/";
    public static final String VM_UPAN_PATH = UPAN_PATH+"vm/";

    public static String getVmId(){
        String vmId = null;
        try {
            vmId = PropertiesUtil.getPropertyValue(LOCALCONFIG_PATH, LOCAL_KEY_VMID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vmId;
    }

}
