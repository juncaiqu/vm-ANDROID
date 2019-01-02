package com.kdx.kdxutils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Author  :qujuncai
 * DATE    :18/11/26
 * Email   :qjchzq@163.com
 */
public class PropertiesUtil {
    public static String getPropertyValue(String fileName, String key) throws Exception {
        String value = null;

        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(key)) {
            throw new Exception("getConfigValue(String fileName, String key) parm can not null");
        }

        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            throw new Exception("fileName illegal");
        }

        if (file.exists() && file.isFile()) {
            Properties prop =getProperty(fileName,"utf-8");
            if (prop.containsKey(key)) {
                value = prop.getProperty(key);
            }
        }
        return value;
    }

    public static Properties getProperty(String fileName, String charset) throws Exception {
        Properties prop = new Properties();
        InputStream in = null;
        InputStreamReader isr = null;
        try {
            in = new BufferedInputStream(new FileInputStream(fileName));
            isr = new InputStreamReader(in, charset);
            prop.load(isr);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

    public static synchronized void setConfigValue(String fileName, String key, String value) throws Exception {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            throw new RuntimeException("setConfigValue(String fileName, String key, value) parm can not null");
        }
        File file = new File(fileName);
        Properties prop = new Properties();
        FileOutputStream os = null;
        FileInputStream fis = null;
        try {
            if (file.exists() && file.isFile()) {
                fis = new FileInputStream(file);
                prop.load(fis);
                fis.close();// 关闭资源
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            prop.setProperty(key, value);
            os = new FileOutputStream(file);
            prop.store(os, "");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
