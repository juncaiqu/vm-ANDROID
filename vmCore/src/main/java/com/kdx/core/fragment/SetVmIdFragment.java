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
import com.kdx.core.utils.ContextUtil;
import com.kdx.core.utils.JsonUtil;
import com.kdx.core.utils.ToastUtils;
import com.kdx.kdxutils.FileUtil;
import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.LocalThreadPoolExecutor;
import com.kdx.kdxutils.MD5Utils;
import com.kdx.kdxutils.NetUtil;
import com.kdx.kdxutils.PropertiesUtil;
import com.kdx.kdxutils.ZipUtils;
import com.kdx.kdxutils.config.GlobalConfig;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


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
        logger.info("SetVmIdFragment onCreateView");
        RestoreFactoryAsyncTask rfat = new RestoreFactoryAsyncTask();
        rfat.execute();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.info("SetVmIdFragment onResume");

    }

    private void findView(View view) {

        iv_qr = (ImageView) view.findViewById(R.id.iv_qr);
        tv_state_info = (TextView) view.findViewById(R.id.tv_state_info);
        bt_refresh = (Button) view.findViewById(R.id.bt_refresh);
        sv_show = (ScrollView) view.findViewById(R.id.sv_show);
        ll_layout = (LinearLayout) view.findViewById(R.id.ll_layout);
        tv_tosetting = (TextView) view.findViewById(R.id.tv_tosetting);
    }

    private void initView() {
        bt_refresh.setOnClickListener(this);
        tv_tosetting.setOnClickListener(this);
        ll_layout.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());
        socketCommunication = SocketCommunication.getSocketCommunication();
        socketCommunication.setCommunicationHandler(this);
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

            case R.id.bt_refresh:
                processStartConnect();
                break;
            case R.id.ll_layout:
                onDisplaySettingButton();
                break;
            case R.id.tv_tosetting:
                intoSetting();
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
        dismissDialog(progressDialog);
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
                boolean isInstallVM = FileUtil.fileExits(GlobalConfig.VM_UPAN_PATH);
                if(isInstallVM){
                    publishProgress("\n正在从U盘复制镜像....");
                    FileUtil.copyDirectiory(GlobalConfig.VM_UPAN_PATH, KdxFileUtil.getRootDir());
                }else{
                    publishProgress("\n正在从本地解压镜像....");
                    boolean isUnzip = ZipUtils.unZipFile(CoreConfig.vmImagePath,"/mnt/sdcard/vm/");
                    if(!isUnzip){
                        return false;
                    }
                }
                File apks = new File(KdxFileUtil.getApksDir());
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
                publishProgress("\n配置文件更新");
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
    }
    private class RestoreFactoryAsyncTask extends AsyncTask<Void, String, Boolean> {

        public RestoreFactoryAsyncTask() {
        }
        @Override
        protected void onPreExecute() {
            setText("正在进行初始化....");
            dismissDialog(progressDialog);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在进行初始化");
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
           try {
               List<String> pageNames = ContextUtil.queryFilterAppInfo(getActivity().getApplicationContext());
               for(String pageName:pageNames){
                   if(pageName.startsWith("com.kdx")){
                       if(!TextUtils.equals(pageName,"com.kdx.core") && !TextUtils.equals(pageName,"com.kdx.install")){
                           CoreActivity coreActivity = (CoreActivity) getActivity();
                           boolean isUninstall = coreActivity.unInstall(pageName);
                           publishProgress("\n卸载程序:"+pageName+(isUninstall?" 成功":" 失败"));
                       }
                   }
               }
               File[] arr_vmDir = new File(KdxFileUtil.getRootDir()).listFiles();
               for(File file_vmDir:arr_vmDir){
                   if(!TextUtils.equals(new File(KdxFileUtil.getLogsDir()).getAbsolutePath(),file_vmDir.getAbsolutePath()) && !TextUtils.equals(new File(KdxFileUtil.getLogsbakDir()).getAbsolutePath(),file_vmDir.getAbsolutePath())){
                       Log.i("core","if:"+file_vmDir.getAbsolutePath());
                       FileUtil.deleteDirectory(file_vmDir);
                   }else{
                       Log.i("core","else:"+file_vmDir.getAbsolutePath());
                   }
               }
           }catch (Exception e){
               e.printStackTrace();
               onProgressUpdate("\n恢复出厂状态出错:"+e.toString());
               return false;
           }

            return true;
        }

        @Override
        protected void onProgressUpdate(String... para){
            appendText(para[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                appendText("\n开始连接服务器...");
                socketCommunication.start();
            }
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


    @Override
    public void onDestroy() {
        dismissDialog(progressDialog);
        super.onDestroy();
        logger.info("SetVmIdFragment onDestroy");
        handler.removeCallbacksAndMessages(null);
        socketCommunication.releaseSocket();


    }
}
