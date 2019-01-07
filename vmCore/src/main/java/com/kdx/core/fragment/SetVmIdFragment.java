package com.kdx.core.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kdx.core.CoreActivity;
import com.kdx.core.R;
import com.kdx.core.config.CoreConfig;
import com.kdx.core.logger.LoggerSetting;
import com.kdx.core.net.CommunicationHandler;
import com.kdx.core.net.SocketCommunication;
import com.kdx.core.net.protocal.tokcs.Ack;
import com.kdx.core.net.protocal.tokcs.InnerCodeReq;
import com.kdx.core.net.protocal.tovm.InnerCodeRpt;
import com.kdx.core.utils.BitMapTool;
import com.kdx.core.utils.JsonUtil;
import com.kdx.core.utils.ToastUtils;
import com.kdx.kdxutils.FileUtil;
import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.MD5Utils;
import com.kdx.kdxutils.NetUtil;
import com.kdx.kdxutils.PropertiesUtil;
import com.kdx.kdxutils.config.GlobalConfig;

import java.io.File;
import java.util.Arrays;


/**
 * Author  :qujuncai
 * DATE    :18/11/26
 * Email   :qjchzq@163.com
 */
@SuppressLint("ValidFragment")
public class SetVmIdFragment extends AbsFragment implements View.OnClickListener, CommunicationHandler {
    private static org.apache.logging.log4j.Logger logger = LoggerSetting.getLogger();
    public static final String TARGET = "SetVmIdFragment";
    private ImageView iv_qr;
    private TextView tv_state_info;
    private SocketCommunication socketCommunication;
    private Button bt_refresh;
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 500;  // 快速点击间隔
    private ProgressDialog progressDialog = null;
    private AlertDialog alertDialog = null;


    private EditText et_vmid = null;
    private Button bt_key_0 = null;
    private Button bt_key_1 = null;
    private Button bt_key_2 = null;
    private Button bt_key_3 = null;
    private Button bt_key_4 = null;
    private Button bt_key_5 = null;
    private Button bt_key_6 = null;
    private Button bt_key_7 = null;
    private Button bt_key_8 = null;
    private Button bt_key_9 = null;
    private Button bt_key_del = null;
    private Button bt_key_clear = null;
    private Button bt_key_confirm = null;
    private Handler handler = new Handler();
    private ScrollView sv_show;
    private LinearLayout ll_layout;
    private TextView tv_tosetting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setvmid_fragment, null);
        ViewGroup.LayoutParams rl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(rl);
        findView(view);
        initView();
        progressDialog = new ProgressDialog(getActivity());
        socketCommunication = SocketCommunication.getSocketCommunication();
        socketCommunication.setCommunicationHandler(this);
        logger.info("SetVmIdFragment onCreateView");
        processStartConnect();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.info("SetVmIdFragment onResume");

    }

    private void findView(View view) {
        et_vmid = (EditText) view.findViewById(R.id.et_vmid);
        bt_key_0 = (Button) view.findViewById(R.id.bt_key_0);
        bt_key_1 = (Button) view.findViewById(R.id.bt_key_1);
        bt_key_2 = (Button) view.findViewById(R.id.bt_key_2);
        bt_key_3 = (Button) view.findViewById(R.id.bt_key_3);
        bt_key_4 = (Button) view.findViewById(R.id.bt_key_4);
        bt_key_5 = (Button) view.findViewById(R.id.bt_key_5);
        bt_key_6 = (Button) view.findViewById(R.id.bt_key_6);
        bt_key_7 = (Button) view.findViewById(R.id.bt_key_7);
        bt_key_8 = (Button) view.findViewById(R.id.bt_key_8);
        bt_key_9 = (Button) view.findViewById(R.id.bt_key_9);
        bt_key_del = (Button) view.findViewById(R.id.bt_key_del);
        bt_key_clear = (Button) view.findViewById(R.id.bt_key_clear);
        bt_key_confirm = (Button) view.findViewById(R.id.bt_key_confirm);
        et_vmid = (EditText) view.findViewById(R.id.et_vmid);
        iv_qr = (ImageView) view.findViewById(R.id.iv_qr);
        tv_state_info = (TextView) view.findViewById(R.id.tv_state_info);
        bt_refresh = (Button) view.findViewById(R.id.bt_refresh);
        sv_show = (ScrollView) view.findViewById(R.id.sv_show);
        ll_layout = (LinearLayout) view.findViewById(R.id.ll_layout);
        tv_tosetting = (TextView) view.findViewById(R.id.tv_tosetting);
    }

    private void initView() {
        bt_key_0.setOnClickListener(this);
        bt_key_1.setOnClickListener(this);
        bt_key_2.setOnClickListener(this);
        bt_key_3.setOnClickListener(this);
        bt_key_4.setOnClickListener(this);
        bt_key_5.setOnClickListener(this);
        bt_key_6.setOnClickListener(this);
        bt_key_7.setOnClickListener(this);
        bt_key_8.setOnClickListener(this);
        bt_key_9.setOnClickListener(this);
        bt_key_del.setOnClickListener(this);
        bt_key_clear.setOnClickListener(this);
        bt_key_confirm.setOnClickListener(this);
        bt_refresh.setOnClickListener(this);
        tv_tosetting.setOnClickListener(this);
        ll_layout.setOnClickListener(this);
    }
    // 需要点击几次 就设置几
    long [] mHits = null;
    public void onDisplaySettingButton() {
        if (mHits == null) {
            mHits = new long[4];
        }
        if(tv_tosetting.getVisibility()!=View.VISIBLE){
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();//记录一个时间
            logger.info(Arrays.toString(mHits));
            if (SystemClock.uptimeMillis() - mHits[0] <= 3000) {//3秒内连续点击。
                logger.info("ok");
                mHits = null;
                tv_tosetting.setVisibility(View.VISIBLE);
                ToastUtils.getInstanc(getActivity().getApplicationContext()).showToast("系统设置入口已打开");
            }
        }
    }
    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME){
            return;
        }
        lastClickTime = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.bt_key_0:
            case R.id.bt_key_1:
            case R.id.bt_key_2:
            case R.id.bt_key_3:
            case R.id.bt_key_4:
            case R.id.bt_key_5:
            case R.id.bt_key_6:
            case R.id.bt_key_7:
            case R.id.bt_key_8:
            case R.id.bt_key_9:
                et_vmid.getText().append(((Button) v).getText().toString());
                if (et_vmid.getText().length() >= 10) {
                    ToastUtils.getInstanc(getActivity().getApplicationContext()).showToast("机器号长度不能大于10位");
                }
                break;
            case R.id.bt_key_clear:
                et_vmid.getText().clear();
                break;
            case R.id.bt_refresh:
                processStartConnect();
                break;
            case R.id.ll_layout:
                onDisplaySettingButton();
                break;
            case R.id.bt_key_del:
                if (et_vmid.getText().length() > 0) {
                    et_vmid.getText().delete(et_vmid.getText().length() - 1, et_vmid.getText().length());
                }
                break;
            case R.id.tv_tosetting:
                intoSetting();
                break;
            case R.id.bt_key_confirm:
                if (et_vmid.getText().length() < 1) {
                    ToastUtils.getInstanc(getActivity().getApplicationContext()).showToast("请输入机器号");
                    return;
                }
                boolean isInstallUpan = FileUtil.fileExits(GlobalConfig.UPAN_PATH);
                if (!isInstallUpan) {
                    ToastUtils.getInstanc(getActivity().getApplicationContext()).showToast("请插入U盘");
                    return;
                }
                boolean isInstallVM = FileUtil.fileExits(GlobalConfig.VM_UPAN_PATH);
                if (!isInstallVM) {
                    ToastUtils.getInstanc(getActivity().getApplicationContext()).showToast("请确认U盘中镜像目录是否存在");
                    return;
                }
                createDialog(et_vmid.getText().toString());
                break;
        }
    }
    private void intoSetting() {
        Intent i = new Intent();
        i.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
        this.getActivity().startActivity(i);
    }




    @Override
    public void msgArrived(SocketCommunication socketCommunication,String msg) {
        logger.info(msg);
        InnerCodeRpt boxStatusResponse = JsonUtil.jsonToObject(msg, InnerCodeRpt.class);
        if(boxStatusResponse.getData() != null){
            InitAsyncTask iat = new InitAsyncTask(boxStatusResponse.getSn(),boxStatusResponse.getData().getInnerCode(),boxStatusResponse.getData().getVmType());
            iat.execute();
        }
    }

    @Override
    public void connected(SocketCommunication socketCommunication) {
        String macAddress = MD5Utils.encodeMD5(NetUtil.getLocalMacAddress().replaceAll(":", ""));
        connectSuccess(macAddress);
        InnerCodeReq innerCodeReq = new InnerCodeReq(false);
        InnerCodeReq.DataBean dataBean = new InnerCodeReq.DataBean();
        dataBean.setMsgType("innerCodeReq");
        dataBean.setMacAddress(macAddress);
        innerCodeReq.setData(dataBean);

        String content = JsonUtil.toJson(innerCodeReq);
        try {
            socketCommunication.sendMsg(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void connectClosed() {
        processStopConnect();
    }

    private  void processStartConnect(){
        setText("正在进行初始化....");
        boolean isInstallUpan = FileUtil.fileExits(GlobalConfig.UPAN_PATH);
        if (!isInstallUpan) {
            appendText("\n未检测到U盘，请插入U盘!!!!");
            dismissDialog(progressDialog);
            return;
        }
        boolean isInstallVM = FileUtil.fileExits(GlobalConfig.VM_UPAN_PATH);
        if (!isInstallVM) {
            appendText("\n未检测到U盘镜像，请确认U盘中镜像目录是否存在vm目录!!!!");
            dismissDialog(progressDialog);
            return;
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在进行初始化");
        progressDialog.show();
        socketCommunication.start();
        appendText("\n开始连接服务器...");
    }
    private void connectSuccess(String macAddress){
        final String qrString = CoreConfig.getInitUrl() + macAddress;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appendText("\n连接服务器成功");
                Bitmap bitmap = BitMapTool.encode2dAsBitmap(qrString, 600, 600, 2);
                iv_qr.setImageBitmap(bitmap);
                iv_qr.setVisibility(View.VISIBLE);
                bt_refresh.setVisibility(View.GONE);
                dismissDialog(progressDialog);

            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SetVmIdFragment.this.socketCommunication.releaseSocket();
            }
        },1000*60);
    }
    private  void processStopConnect(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appendText("\n断开连接");
                iv_qr.setImageBitmap(null);
                iv_qr.setVisibility(View.GONE);
                bt_refresh.setVisibility(View.VISIBLE);
                dismissDialog(progressDialog);
            }
        });
    }
    private void setText(String text){
        tv_state_info.setText(text);
        if (sv_show == null || ll_layout == null) {
            return;
        }
        int offset = ll_layout.getMeasuredHeight() - sv_show.getMeasuredHeight();
        if (offset < 0) {
            offset = 0;
        }
        sv_show.scrollTo(0, offset);
    }
    private void appendText(String text){
        tv_state_info.append(text);
        if (sv_show == null || ll_layout == null) {
            return;
        }
        int offset = ll_layout.getMeasuredHeight() - sv_show.getMeasuredHeight();
        if (offset < 0) {
            offset = 0;
        }
        sv_show.scrollTo(0, offset);
    }
    private class InitAsyncTask extends AsyncTask<Void, String, Boolean> {
        private String vmId = null;
        private int vmType;
        private long sn;

        public InitAsyncTask(long sn,String vmId,int vmType) {
            this.vmId = vmId;
            this.vmType = vmType;
            this.sn = sn;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = true;
            Ack ack = new Ack(sn);
            ack.setSuccess(0);
            try {
                socketCommunication.sendMsg(JsonUtil.toJson(ack));

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                publishProgress("\n配置机器号为:"+vmId+",机型:"+vmType);
                /*FileUtil.deleteDirectory(new File(KdxFileUtil.getApksDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getAppsDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getConfigDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getCoreDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getDataDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getLibsDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getUpdateDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getResourceDir()));*/
                File[] arr_vmDir = new File(KdxFileUtil.getRootDir()).listFiles();
                for(File file_vmDir:arr_vmDir){
                    if(!TextUtils.equals(new File(KdxFileUtil.getLogsDir()).getAbsolutePath(),file_vmDir.getAbsolutePath()) && !TextUtils.equals(new File(KdxFileUtil.getLogsbakDir()).getAbsolutePath(),file_vmDir.getAbsolutePath())){
                        Log.i("core","if:"+file_vmDir.getAbsolutePath());
                        FileUtil.deleteDirectory(file_vmDir);
                    }else{
                        Log.i("core","else:"+file_vmDir.getAbsolutePath());
                    }
                }
                FileUtil.copyDirectiory(GlobalConfig.VM_UPAN_PATH+"apks/", KdxFileUtil.getApksDir());
                File apks = new File(KdxFileUtil.getApksDir());
                if(!apks.exists() || !apks.isDirectory()){
                    return false;
                }
                File[] listFile = apks.listFiles();
                publishProgress("\n开始安装程序,共"+listFile.length+"个");
                int prog = 1;
                for (File apkPath:listFile){
                    CoreActivity coreActivity = (CoreActivity) getActivity();
                    boolean installRes = coreActivity.install(apkPath.getAbsolutePath());
                    publishProgress("\n安装进度"+(prog++)+"/"+listFile.length+"个","update");
                    if(!installRes){
                        return false;
                    }
                }
                publishProgress("\n正在从U盘复制镜像");
                FileUtil.copyDirectiory(GlobalConfig.VM_UPAN_PATH, KdxFileUtil.getRootDir());
                PropertiesUtil.setConfigValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_VMID, vmId);
                String oldVmidConfig = "{\"innerCode\":\""+vmId+"\"}";
                FileUtil.writeFile(KdxFileUtil.getConfigDir()+"VmBaseInfo.json",oldVmidConfig);
                PropertiesUtil.setConfigValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_VMTYPE, String.valueOf(vmType));


                publishProgress("\n安装完成");
                publishProgress("\n正在进入售卖模式");
                SystemClock.sleep(1000*5);
                PropertiesUtil.setConfigValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_ISINSTALL, GlobalConfig.LOCAL_ISINSTALL_YES);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    PropertiesUtil.setConfigValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_ISINSTALL, GlobalConfig.LOCAL_ISINSTALL_NO);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                publishProgress("\n错误信息:"+e.toString());
                result = false;
            }
            return result;
        }
        @Override
        protected void onProgressUpdate(String... para){
            if(para.length>=2){
                String str_content = tv_state_info.getText().toString();
                int index = str_content.indexOf("安装进度");
                if(index != -1){
                    str_content = str_content.substring(0,index-1);
                }
                setText(str_content+para[0]);

            }else{
                appendText(para[0]);
            }

        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                CoreActivity coreActivity = (CoreActivity) getActivity();
                logger.info("onPostExecute = {}",coreActivity);
                coreActivity.switchView();
            } else {
                appendText("\n初始化机器出现错误,请重试");
            }

        }

        @Override
        protected void onPreExecute() {

        }
    }
    private void dismissDialog(Dialog dialog){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
    public SetVmIdFragment() {
        super(TARGET);
    }
    /**-----------------------------------------------------------------*/
    private void createDialog(final String vmId) {
        if (alertDialog == null || !alertDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("设置提醒");
            builder.setMessage("当前设置的售货机编号为 : " + vmId);
            builder.setCancelable(false);


            builder.setPositiveButton(R.string.dialog_confirm,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            alertDialog.dismiss();
                            FileAsyncTask fileAsyncTask = new FileAsyncTask(vmId);
                            fileAsyncTask.execute();
                        }
                    });

            builder.setNegativeButton(R.string.dialog_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int which) {
                            alertDialog.dismiss();
                        }
                    });
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }
    private class FileAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog dialog = null;
        private String vmId = null;

        public FileAsyncTask(String vmId) {
            this.vmId = vmId;
            dialog = new ProgressDialog(getActivity());
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = true;
            try {
                FileUtil.deleteDirectory(new File(KdxFileUtil.getApksDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getAppsDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getConfigDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getCoreDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getDataDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getLibsDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getUpdateDir()));
                FileUtil.deleteDirectory(new File(KdxFileUtil.getResourceDir()));
                FileUtil.copyDirectiory(GlobalConfig.VM_UPAN_PATH, KdxFileUtil.getRootDir());
                PropertiesUtil.setConfigValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_VMID, vmId);

                int prog = 1;
                while (prog < 50) {
                    SystemClock.sleep(100);
                    prog++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if (result) {
                CoreActivity coreActivity = (CoreActivity) getActivity();
                coreActivity.toVmTypeView();
            } else {
                ToastUtils.getInstanc(getActivity().getApplicationContext()).showToast("配置机器号出现错误");
            }

        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setTitle("提示");
            dialog.setMessage("正在从U盘中拷贝镜像文件");
            dialog.show();
        }


    }

    @Override
    public void onDestroy() {
        dismissDialog(progressDialog);
        super.onDestroy();
        logger.info("SetVmIdFragment onDestroy");
        handler.removeCallbacksAndMessages(null);
        socketCommunication.releaseSocket();


    }
}
