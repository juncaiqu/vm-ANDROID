package com.kdx.cmdservice;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.kdx.cmdservice.logger.LoggerSetting;
import com.kdx.cmdservice.model.StatusInfo;
import com.kdx.kdxutils.FileUtil;
import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.SdcardUtils;

import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class CmdApplication extends Application {
    private static CmdApplication singleInstance;
    private MyPhoneStateListener myPhoneListener;
    private TelephonyManager telManager;
    private HashMap<String, String> rssiMap = new HashMap<String, String>();
    private String resi;
    @Override
    public void onCreate() {
        super.onCreate();
        singleInstance = this;
        myPhoneListener = new MyPhoneStateListener();

        telManager = (TelephonyManager) singleInstance.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(myPhoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    public static CmdApplication getInstance(){return singleInstance;}
    /**
     * @return 系统版本
     */
    public static String getSystemVersion(){
        return android.os.Build.VERSION.RELEASE;
    }
    public String getStatusInfo(){
        StatusInfo statusInfos = new StatusInfo(getSimOperator(), getSrri(),android.os.Build.SERIAL, SdcardUtils.getSdRoom(), SdcardUtils.testSD(KdxFileUtil.getRootDir()+"test.txt"), getMemory(), FileUtil.readUsage()+"%", getPix(), getSystemVersion(), Build.DEVICE,android.os.Build.DISPLAY);
        return statusInfos.toString();
    }
    private String getPix() {
        DisplayMetrics metrics = this.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        return screenWidth+"X"+screenHeight;
    }
    /**
     * 获得内存占用率
     * @return
     */
    private String getMemory(){
        StringBuilder sb = new StringBuilder();
        sb.append(SdcardUtils.getAvailMemory(this)).append("/").append(SdcardUtils.getTotalMemory(this));
        return sb.toString();
    }
    private final class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            int cdmaDbm = signalStrength.getCdmaDbm();
            System.out.println("MyPhoneStateListener");
            if (getSimOperator().equals("中国联通")) {
                int asu = signalStrength.getGsmSignalStrength();
                int dBm = -113 + 2 * asu;
                String rssi = String.valueOf(dBm) + "dBm,"
                        + String.valueOf(asu + "asu");
                rssiMap.put("rssi", rssi);
            } else if (getSimOperator().equals("中国电信")) {
                int evdoDbm = signalStrength.getEvdoDbm();
                String rssi = String.valueOf(cdmaDbm) + "dBm,"
                        + String.valueOf(evdoDbm + "EvdodBm");
                rssiMap.put("rssi", rssi);
            } else {
                String rssi = String.valueOf(cdmaDbm) + "dBm";
                rssiMap.put("rssi", rssi);
            }
        }
    };
    private String getSimOperator() {
        // 获取运营商名字
        String operator = telManager.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002")
                    || operator.equals("46007")) {
                return "中国移动";
            } else if (operator.equals("46001")) {
                return "中国联通";
            } else if (operator.equals("46003")) {
                return "中国电信";
            }
        }
        return "未知";
    }
    private String getSrri() {

        resi = rssiMap.get("rssi");

        if (myPhoneListener != null)
            telManager.listen(myPhoneListener, PhoneStateListener.LISTEN_NONE);

        return resi + "";

    }
    /**
     *  //BOARD 主板
     String phoneInfo = "BOARD: " + android.os.Build.BOARD;
     phoneInfo += ", BOOTLOADER: " + android.os.Build.BOOTLOADER;
     //BRAND 运营商
     phoneInfo += ", BRAND: " + android.os.Build.BRAND;
     phoneInfo += ", CPU_ABI: " + android.os.Build.CPU_ABI;
     phoneInfo += ", CPU_ABI2: " + android.os.Build.CPU_ABI2;

     //DEVICE 驱动
     phoneInfo += ", DEVICE: " + android.os.Build.DEVICE;
     //DISPLAY Rom的名字 例如 Flyme 1.1.2（魅族rom） &nbsp;JWR66V（Android nexus系列原生4.3rom）
     phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY;
     //指纹
     phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT;
     //HARDWARE 硬件
     phoneInfo += ", HARDWARE: " + android.os.Build.HARDWARE;
     phoneInfo += ", HOST: " + android.os.Build.HOST;
     phoneInfo += ", ID: " + android.os.Build.ID;
     //MANUFACTURER 生产厂家
     phoneInfo += ", MANUFACTURER: " + android.os.Build.MANUFACTURER;
     //MODEL 机型
     phoneInfo += ", MODEL: " + android.os.Build.MODEL;
     phoneInfo += ", PRODUCT: " + android.os.Build.PRODUCT;
     phoneInfo += ", RADIO: " + android.os.Build.RADIO;
     phoneInfo += ", RADITAGSO: " + android.os.Build.TAGS;
     phoneInfo += ", TIME: " + android.os.Build.TIME;
     phoneInfo += ", TYPE: " + android.os.Build.TYPE;
     phoneInfo += ", USER: " + android.os.Build.USER;
     //VERSION.RELEASE 固件版本
     phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
     phoneInfo += ", VERSION.CODENAME: " + android.os.Build.VERSION.CODENAME;
     //VERSION.INCREMENTAL 基带版本
     phoneInfo += ", VERSION.INCREMENTAL: " + android.os.Build.VERSION.INCREMENTAL;
     //VERSION.SDK SDK版本
     phoneInfo += ", VERSION.SDK: " + android.os.Build.VERSION.SDK;
     phoneInfo += ", VERSION.SDK_INT: " + android.os.Build.VERSION.SDK_INT;
     logger.info(phoneInfo);
     */
}
