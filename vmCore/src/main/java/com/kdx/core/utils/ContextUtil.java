package com.kdx.core.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author  :qujuncai
 * DATE    :18/12/14
 * Email   :qjchzq@163.com
 */
public class ContextUtil {
    public static void sendBroadCast(Context context, String action){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(intent);
    }

    /**
     * 获得android系统sdk版本号
     *
     * @param context
     * @return
     */
    public static int getOSVersionSDK(Context context) {
        return android.os.Build.VERSION.SDK_INT; // SDK号
    }

    /**
     * 获得androidSDK版本 例:4.0.3
     *
     * @param context
     * @return
     */
    public static String getOSVersionSDKRELEASE(Context context) {
        return android.os.Build.VERSION.RELEASE;
    }


    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(new Date(System.currentTimeMillis()));
        return format;
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * // 取得文件大小
     *
     * @param
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static long getFileSizes(String dir, String fileName)
            throws IOException {
        File f = new File(dir, fileName);
        long s = 0L;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
            fis.close();
        } else {
            return -1;
        }
        return s;
    }



    /**
     * 获得已安装用户程序的名称,包名与版本信息
     */
    public static List<Map<String, Object>> getInstallAppInfo(Context context) {

        PackageManager pManager = context.getPackageManager();
        List<PackageInfo> appList = getAllApps(context);
        ArrayList<Map<String, Object>> infos = new ArrayList<Map<String, Object>>();

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("appName", "名称");
        map1.put("versionName", "当前版本");
        infos.add(map1);

        for (int i = 0; i < appList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
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

    /**
     * 查询所有已经安装的应用程序
     *
     * @param ctx
     * @return
     */
    public static List<String> queryFilterAppInfo(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的
        List<String> mList = new ArrayList<String>();
        for (ApplicationInfo app : listAppcations) {
            boolean isSystem = filterApp(app);

            if (isSystem) {
                String appPackName = app.packageName;
                String pkName_pinyin = "com.google.android.inputmethod.pinyin";
                if (!TextUtils.isEmpty(appPackName)
                        && !appPackName.equals(pkName_pinyin)) {
                    mList.add(appPackName);
                }
            }
        }
        return mList;
    }

    /**
     * 判断是否是系统自带的程序,是就返回false;不是就返回true;
     *
     */
    public static boolean filterApp(ApplicationInfo info) {
        // 是系统的程序,但该程序因为升级已经变成了下载更新程序,所以不属于系统默认程序了
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
            // 不是系统自带程序
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }





    /**
     * 获取未安装的APK信息
     *
     * @param context
     * @param archiveFilePath
     *            APK文件的路径。如：/sdcard /download/XX.apk
     */
    public static String getUninatllApkInfo(Context context,
                                            String archiveFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            String packageName = appInfo.packageName;
            return packageName;
        }
        return null;
    }

}
