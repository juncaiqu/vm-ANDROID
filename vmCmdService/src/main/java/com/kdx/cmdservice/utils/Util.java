package com.kdx.cmdservice.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class Util {
    public static List<Map<String, String>> getInstallAppInfo(Context context) {

        PackageManager pManager = context.getPackageManager();
        List<PackageInfo> appList = getAllApps(context);
        ArrayList<Map<String, String>> infos = new ArrayList<Map<String, String>>();

        for (int i = 0; i < appList.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            PackageInfo pinfo = appList.get(i);
            ApplicationInfo appInfo = pinfo.applicationInfo;
            // 判断是不是手机预安装的程序 <= 0（非预安装程序）
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {

                String appName = (String) pManager.getApplicationLabel(
                        pinfo.applicationInfo).toString();
                // String PackageName = pinfo.packageName.toString();
                String versionName = pinfo.versionName;
                map.put("versionName", versionName);
                map.put("appName", appName);
                infos.add(map);
            }
        }
        return infos;
    }
    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            apps.add(pak);
        }
        return apps;
    }


}
