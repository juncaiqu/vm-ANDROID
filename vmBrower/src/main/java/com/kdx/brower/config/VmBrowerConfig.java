package com.kdx.brower.config;

import android.text.TextUtils;

import com.kdx.kdxutils.FileUtil;
import com.kdx.kdxutils.KdxFileUtil;
import org.json.JSONObject;

/**
 * Created by qujc-pc on 2015/12/3.
 */
public class VmBrowerConfig {
    //public final static String defaultUrl = "http://localhost:8080/index.html#home";
    public final static String defaultUrl = "http://localhost:8080/index.html#home";
    public static String getMainUrl(){
        String content = FileUtil.readFile(KdxFileUtil.getConfigDir()+"vmbrower/package.json");
        try {
            if(!TextUtils.isEmpty(content)) {
                JSONObject jsonObject = new JSONObject(content);
                String mainUrl = jsonObject.getString("main");
                if (!TextUtils.isEmpty(mainUrl)) {
                    return mainUrl;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return VmBrowerConfig.defaultUrl;
    }
}
