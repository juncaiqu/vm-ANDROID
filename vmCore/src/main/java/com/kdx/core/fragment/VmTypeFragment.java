package com.kdx.core.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kdx.core.CoreActivity;
import com.kdx.core.R;
import com.kdx.core.logger.LoggerSetting;
import com.kdx.core.utils.ToastUtils;
import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.PropertiesUtil;
import com.kdx.kdxutils.config.GlobalConfig;


import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Author  :qujuncai
 * DATE    :18/11/26
 * Email   :qjchzq@163.com
 */
@SuppressLint("ValidFragment")
public class VmTypeFragment extends AbsFragment implements RadioGroup.OnCheckedChangeListener ,View.OnClickListener{
    public static final String TARGET = "VmTypeFragment";
    public VmTypeFragment() {
        super(TARGET);
    }
    private static org.apache.logging.log4j.Logger logger = LoggerSetting.getLogger();
    private RadioGroup rg_vmtypes = null;
    private InstallAsyncTask installAsyncTask = null;
    private final int FILE_MAX = 1;
    private final int FILE_UPDATE = 2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vmtype_fragment, null);
        ViewGroup.LayoutParams rl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(rl);
        rg_vmtypes = (RadioGroup)view.findViewById(R.id.rg_vmtypes);
        Button bt_vtf_confirm = (Button)view.findViewById(R.id.bt_vtf_save);
        bt_vtf_confirm.setOnClickListener(this);
        try {
            Properties property = PropertiesUtil.getProperty(KdxFileUtil.getConfigDir() + "core/vmType.properties","utf-8");
            Set<Map.Entry<Object, Object>> entrySet = property.entrySet();//返回的属性键值对实体
            for (Map.Entry<Object, Object> entry : entrySet) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
                RadioButton radioButton = new RadioButton(this.getActivity().getApplicationContext());
                radioButton.setTag(entry.getKey());
                radioButton.setText(entry.getValue().toString());
                rg_vmtypes.addView(radioButton);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rg_vmtypes.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
        logger.debug("tag={}",radioButton.getTag());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_vtf_save:
                RadioButton radioButton = (RadioButton)rg_vmtypes.findViewById(rg_vmtypes.getCheckedRadioButtonId());
                String vmType = "default";
                if(radioButton == null){
                }else{
                    vmType = (String)radioButton.getTag();
                }
                if(installAsyncTask==null || !installAsyncTask.isExecute){
                    installAsyncTask = new InstallAsyncTask();
                    installAsyncTask.execute(vmType);
                }
                break;
        }
    }
    private class InstallAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private ProgressDialog dialog = null;
        private boolean isExecute = false;
        public InstallAsyncTask(){
            dialog = new ProgressDialog(getActivity());
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean returnValue = true;
            try {
                PropertiesUtil.setConfigValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_VMTYPE, params[0]);
                int prog = 1;
                publishProgress(FILE_MAX,50);
                while (prog < 50) {
                    SystemClock.sleep(100);
                    prog++;
                    publishProgress(FILE_UPDATE,prog);
                }
                PropertiesUtil.setConfigValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_ISINSTALL, GlobalConfig.LOCAL_ISINSTALL_YES);
            } catch (Exception e) {
                e.printStackTrace();
                returnValue=false;
                try {
                    PropertiesUtil.setConfigValue(GlobalConfig.LOCALCONFIG_PATH, GlobalConfig.LOCAL_KEY_ISINSTALL, GlobalConfig.LOCAL_ISINSTALL_NO);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return returnValue;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            CoreActivity coreActivity = (CoreActivity)getActivity();
            logger.debug("coreActivity={}",coreActivity);
            if(result){
                coreActivity.switchView();
            }else{
                ToastUtils.getInstanc(getActivity().getApplicationContext()).showToast("安装程序失败,请检查安装文件是否完整");
                coreActivity.toSetVmIdView();
            }
            isExecute = false;
        }

        @Override
        protected void onPreExecute() {
            isExecute = true;
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setTitle("智能安装系统");
            dialog.setMessage("正在安装镜像程序");
            dialog.show();
        }
        @Override
        protected void onProgressUpdate(Integer... process){
            int type = process[0];
            int value = process[1];
            logger.debug("type={},value={}",type,value);
            if(type == FILE_MAX){
                dialog.setMessage("共计" + value + "个程序需要安装");
                dialog.setMax(value);
            }else if(type == FILE_UPDATE){
                dialog.setMessage("已安装完成" + value + "个程序");
                dialog.setProgress(value);
            }
        }

    }
}
