package com.kdx.core.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.kdx.core.CoreActivity;
import com.kdx.core.R;
import com.kdx.core.config.CoreConfig;
import com.kdx.core.config.MaintainType;
import com.kdx.core.logger.LoggerSetting;
import com.kdx.core.ov.StateInfo;
import com.kdx.core.utils.ContextUtil;
import com.kdx.core.utils.ToastUtils;
import com.kdx.core.view.MaintainAdapter;
import com.kdx.kdxutils.FileUtil;
import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.PropertiesUtil;
import com.kdx.kdxutils.SdcardUtils;
import com.kdx.kdxutils.config.ActionConfig;
import com.kdx.kdxutils.config.GlobalConfig;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author  :qujuncai
 * DATE    :18/11/26
 * Email   :qjchzq@163.com
 */
@SuppressLint("ValidFragment")
public class MaintainFragment extends AbsFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String TARGET = "MaintainFragment";
    private static Logger logger = LoggerSetting.getLogger();
    private static final int STA_MAINTAIN_TOOLS = 0x00;
    private static final int STA_STATE_INFO = 0x01;
    private int currentSta = STA_MAINTAIN_TOOLS;
    private Button bt_maintain_tools;
    private Button bt_state_info;
    private ListView lv_content;
    private Button bt_update;
    private Button bt_reboot;
    private Map<Integer, StateInfo> stateMap = null;
    private Map<Integer, StateInfo> toolsMap = null;
    private List<StateInfo> stateInfoList = null;
    private List<StateInfo> toolsList = null;
    private MaintainAdapter maintainAdapter;
    private MyAsyncTask asyncTask;
    private TelephonyManager telManager;
    public static final String UNKNOWN = "未知";
    public static final String CMCC = "中国移动";
    public static final String CDMA = "中国电信";
    public static final String WCDMA = "中国联通";
    private AlertDialog dialog;
    private static final int DIALOG_DISMISS = 0x12;
    private static final int DIALOG_REBOOT = 0x13;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MaintainType.tools_item_configure:

                    break;
                case DIALOG_DISMISS:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;
                case DIALOG_REBOOT:

                    break;
            }
        }
    };

    public MaintainFragment() {
        super(TARGET);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maintain_fragment, null);
        ViewGroup.LayoutParams rl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(rl);
        findView(view);
        initView();
        initData();
        return view;
    }


    private void findView(View view) {
        bt_maintain_tools = (Button) view.findViewById(R.id.bt_maintain_tools);
        bt_state_info = (Button) view.findViewById(R.id.bt_state_info);
        bt_update = (Button) view.findViewById(R.id.bt_update);
        bt_reboot = (Button) view.findViewById(R.id.bt_reboot);
        lv_content = (ListView) view.findViewById(R.id.lv_content);
    }

    private void initView() {
        bt_maintain_tools.setOnClickListener(this);
        bt_state_info.setOnClickListener(this);
        bt_update.setOnClickListener(this);
        bt_reboot.setOnClickListener(this);
    }

    private void initData() {
        telManager = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        stateMap = new HashMap<Integer, StateInfo>();
        stateMap.put(MaintainType.state_item_vmidInfo, new StateInfo(getActivity().getString(R.string.state_item_vmidInfo), null, MaintainType.state_item_vmidInfo));
        stateMap.put(MaintainType.state_item_vmType, new StateInfo(getActivity().getString(R.string.state_item_vmType), null, MaintainType.state_item_vmType));
        stateMap.put(MaintainType.state_item_imgType, new StateInfo(getActivity().getString(R.string.state_item_imgType), null, MaintainType.state_item_imgType));
        stateMap.put(MaintainType.state_item_serialInfo, new StateInfo(getActivity().getString(R.string.state_item_serialInfo), null, MaintainType.state_item_serialInfo));
        stateMap.put(MaintainType.state_item_signalInfo, new StateInfo(getActivity().getString(R.string.state_item_signalInfo), null, MaintainType.state_item_signalInfo));
        stateMap.put(MaintainType.state_item_simInfo, new StateInfo(getActivity().getString(R.string.state_item_simInfo), null, MaintainType.state_item_simInfo));
        stateMap.put(MaintainType.state_item_cpuInfo, new StateInfo(getActivity().getString(R.string.state_item_cpuInfo), null, MaintainType.state_item_cpuInfo));
        stateMap.put(MaintainType.state_item_sdcardInfo, new StateInfo(getActivity().getString(R.string.state_item_sdcardInfo), null, MaintainType.state_item_sdcardInfo));
        stateMap.put(MaintainType.state_item_memoryInfo, new StateInfo(getActivity().getString(R.string.state_item_memoryInfo), null, MaintainType.state_item_memoryInfo));
        stateInfoList = new ArrayList<StateInfo>(stateMap.values());
        Collections.sort(stateInfoList);
        toolsMap = new HashMap<Integer, StateInfo>();
        toolsMap.put(MaintainType.tools_item_bak, new StateInfo(getActivity().getString(R.string.tools_item_bak), null, MaintainType.tools_item_bak));
        toolsMap.put(MaintainType.tools_item_copy, new StateInfo(getActivity().getString(R.string.tools_item_copy), null, MaintainType.tools_item_copy));
        toolsMap.put(MaintainType.tools_item_configure, new StateInfo(getActivity().getString(R.string.tools_item_configure), null, MaintainType.tools_item_configure));
        toolsMap.put(MaintainType.tools_item_rebootpro, new StateInfo(getActivity().getString(R.string.tools_item_rebootpro), null, MaintainType.tools_item_rebootpro));
        toolsMap.put(MaintainType.tools_item_verinfo, new StateInfo(getActivity().getString(R.string.tools_item_verinfo), null, MaintainType.tools_item_verinfo));
        toolsMap.put(MaintainType.tools_item_nettest, new StateInfo(getActivity().getString(R.string.tools_item_nettest), null, MaintainType.tools_item_nettest));
        toolsMap.put(MaintainType.tools_item_setting, new StateInfo(getActivity().getString(R.string.tools_item_setting), null, MaintainType.tools_item_setting));
        toolsMap.put(MaintainType.tools_item_otherset, new StateInfo(getActivity().getString(R.string.tools_item_otherset), null, MaintainType.tools_item_otherset));
        toolsList = new ArrayList<StateInfo>(toolsMap.values());
        Collections.sort(toolsList);
        maintainAdapter = new MaintainAdapter(getActivity().getApplicationContext());
        if(currentSta == STA_STATE_INFO){
            maintainAdapter.setDateList(stateInfoList);
        }else{
            maintainAdapter.setDateList(toolsList);
        }
        lv_content.setAdapter(maintainAdapter);
        lv_content.setOnItemClickListener(this);
        telManager.listen(new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                StateInfo stateInfo = stateMap.get(MaintainType.state_item_simInfo);
                String value = "";
                String operator = stateMap.get(MaintainType.state_item_simInfo).getStateValue();
                int cdmaDbm = signalStrength.getCdmaDbm();
                if (TextUtils.equals(WCDMA, operator)) {
                    int asu = signalStrength.getGsmSignalStrength();
                    int dBm = -113 + 2 * asu;
                    value = String.valueOf(dBm) + " dBm   " + String.valueOf(asu + " asu");
                } else if (TextUtils.equals(CDMA, operator)) {
                    int evdoDbm = signalStrength.getEvdoDbm();
                    value = String.valueOf(cdmaDbm) + " dBm   " + String.valueOf(evdoDbm + " Evdo dBm");
                } else {
                    value = String.valueOf(cdmaDbm) + " dBm";
                }
                stateInfo.setStateValue(value);
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        asyncTask = new MyAsyncTask();
        asyncTask.execute();
    }


    private void intoSetting() {
        Intent i = new Intent();
        i.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
        this.getActivity().startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_maintain_tools:
                currentSta = STA_MAINTAIN_TOOLS;
                maintainAdapter.setDateList(toolsList);
                maintainAdapter.notifyDataSetChanged();
                break;
            case R.id.bt_state_info:
                currentSta = STA_STATE_INFO;
                maintainAdapter.setDateList(stateInfoList);
                maintainAdapter.notifyDataSetChanged();
                break;
            case R.id.bt_update:
                asyncTask = new MyAsyncTask();
                asyncTask.execute();
                break;
            case R.id.bt_reboot:
                showDialog("提示", "是否要重启工控", "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContextUtil.sendBroadCast(getActivity().getApplicationContext(), ActionConfig.B_REBOOT);

                    }
                });
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StateInfo item = maintainAdapter.getItem(position);
        switch (item.getOpType()) {
            case MaintainType.tools_item_bak:
                boolean isInstallUpan = FileUtil.fileExits(GlobalConfig.UPAN_PATH);
                if (!isInstallUpan) {
                    showDialog("提醒", "请插入U盘", "确定", null);
                }else{
                    FileAsyncTask fileAsyncTask = new FileAsyncTask(getActivity());
                    fileAsyncTask.setAsyncTaskCallback(new FileAsyncTask.AsyncTaskCallback() {
                        @Override
                        public boolean doInBackground() {
                            boolean result = true;
                            try {
                                FileUtil.copyDirectiory(KdxFileUtil.getRootDir(), GlobalConfig.VM_UPAN_PATH);
                            } catch (Exception e) {
                                e.printStackTrace();
                                result = false;
                            }
                            return result;
                        }
                    });
                    fileAsyncTask.execute();
                }

                break;
            case MaintainType.tools_item_copy:
                if (!FileUtil.fileExits(GlobalConfig.VM_UPAN_PATH+"resource/")) {
                    showDialog("提醒", "U盘resouce目录不存在", "确定", null);
                }else{
                    FileAsyncTask fileAsynccopy = new FileAsyncTask(getActivity());
                    fileAsynccopy.setAsyncTaskCallback(new FileAsyncTask.AsyncTaskCallback() {
                        @Override
                        public boolean doInBackground() {
                            boolean result = true;
                            try {
                                FileUtil.copyDirectiory( GlobalConfig.VM_UPAN_PATH+"resource/",KdxFileUtil.getResourceDir());
                            } catch (Exception e) {
                                e.printStackTrace();
                                result = false;
                            }
                            return result;
                        }
                    });
                    fileAsynccopy.execute();
                }

                break;
            case MaintainType.tools_item_configure:
                showDialog("提醒", "是否进行重新配置，确定后将清空本机配置", "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FileUtil.deleteDirectory(new File(KdxFileUtil.getConfigDir()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        CoreActivity coreActivity = (CoreActivity) getActivity();
                        coreActivity.switchView();
                    }
                });

                break;
            case MaintainType.tools_item_rebootpro:
                try {
                    String[] command = { "/system/bin/sh","-c", "busybox pkill com.kdx" };
                    Runtime.getRuntime().exec(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MaintainType.tools_item_verinfo:
                // 获得正在运行的用户程序
                List<Map<String, Object>> installAppInfo = ContextUtil.getInstallAppInfo(getActivity().getApplicationContext());
                if (installAppInfo.size() == 0 || installAppInfo == null) {
                    ToastUtils.getInstanc(getActivity().getApplicationContext()).showToast("本机目前未按装任何用户程序!");
                }
                SimpleAdapter adapter1 = new SimpleAdapter(getActivity().getApplicationContext(),installAppInfo, R.layout.apk_version, new String[] {"appName", "versionName" }, new int[] {R.id.tv_name, R.id.tv_ver });
                createInfoDialog(adapter1,"软件版本信息" );
                break;
            case MaintainType.tools_item_nettest:
                Uri uri = Uri.parse("http://www.kangdexin.com");
                Intent web = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(web);
                break;
            case MaintainType.tools_item_setting:
                intoSetting();
                break;
            case MaintainType.tools_item_otherset:
                String otherSetName = CoreConfig.getOthersetAction();
                try{
                    Intent intent = new Intent(otherSetName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                }catch(Exception e){
                    ToastUtils.getInstanc(getActivity().getApplicationContext()).showToast( otherSetName+"程序没有安装");
                }
                break;
        }
    }

    /**
     * 创建一个对话框
     */
    private void showDialog(String title, String msg, String posBtn, String negBtn, DialogInterface.OnClickListener dilogListener) {
        if (dialog == null || !dialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(posBtn, dilogListener);

            // 点击取消
            builder.setNegativeButton(negBtn,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int which) {
                            d.dismiss();
                        }
                    });
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void showDialog(String title, String msg, String posBtn, DialogInterface.OnClickListener dilogListener) {
        if (dialog == null || !dialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(posBtn, dilogListener);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }
    private  void createInfoDialog(Adapter adapter, String title) {
        if (dialog == null || !dialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle(title);
            builder.setAdapter((ListAdapter) adapter, null);
            builder.setPositiveButton("确认", null);
            dialog = builder.create();
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }


    }
    class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            for (StateInfo stateInfo : stateInfoList) {
                String value = "";
                switch (stateInfo.getOpType()) {
                    case MaintainType.state_item_vmidInfo:
                        try {
                            value = PropertiesUtil.getPropertyValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_VMID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case MaintainType.state_item_vmType:
                        value = "未知";
                        try {
                            value = PropertiesUtil.getPropertyValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_VMTYPE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case MaintainType.state_item_imgType:
                        String imgType = null;
                        try {
                            imgType = PropertiesUtil.getPropertyValue(KdxFileUtil.getConfigDir() + "config.properties", GlobalConfig.CONFIG_KEY_IMGTYPE);
                        } catch (Exception e) {
                        }
                        if (TextUtils.isEmpty(imgType)) {
                            value = "vm";
                        } else {
                            value = imgType;
                        }
                        break;
                    case MaintainType.state_item_serialInfo:
                        value = android.os.Build.SERIAL;
                        break;
                    case MaintainType.state_item_signalInfo:

                        break;
                    case MaintainType.state_item_simInfo:
                        String operator = telManager.getSimOperator();
                        if (operator != null) {
                            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                                value = CMCC;
                            } else if (operator.equals("46001")) {
                                value = WCDMA;
                            } else if (operator.equals("46003")) {
                                value = CDMA;
                            } else {
                                value = UNKNOWN;
                            }
                        } else {
                            value = UNKNOWN;
                        }
                        break;
                    case MaintainType.state_item_cpuInfo:
                        value = FileUtil.readUsage() + "%";
                        break;
                    case MaintainType.state_item_memoryInfo:
                        String availMemory = SdcardUtils.getAvailMemory(getActivity().getApplicationContext());
                        String totalMemory = SdcardUtils.getTotalMemory(getActivity().getApplicationContext());
                        value = "可用:" + availMemory + " / 共计:" + totalMemory;
                        break;
                    case MaintainType.state_item_sdcardInfo:
                        value = "可用:" + SdcardUtils.getAvailaSize(KdxFileUtil.getSdcardDir()) + " / 共计:" + SdcardUtils.getTotalSize(KdxFileUtil.getSdcardDir());
                        break;
                }
                stateInfo.setStateValue(value);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            maintainAdapter.notifyDataSetChanged();

        }
    }


}
