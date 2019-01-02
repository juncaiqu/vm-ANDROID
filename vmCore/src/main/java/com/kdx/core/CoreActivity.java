package com.kdx.core;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.kdx.core.config.CoreConfig;
import com.kdx.core.exception.CoreExceptionHandler;
import com.kdx.core.fragment.AbsFragment;
import com.kdx.core.fragment.MaintainFragment;
import com.kdx.core.fragment.SetVmIdFragment;
import com.kdx.core.fragment.VmTypeFragment;
import com.kdx.core.logger.LoggerSetting;
import com.kdx.core.utils.ContextUtil;
import com.kdx.core.utils.ToastUtils;
import com.kdx.install.IInstallInterface;
import com.kdx.kdxutils.PropertiesUtil;
import com.kdx.kdxutils.config.ActionConfig;
import com.kdx.kdxutils.config.GlobalConfig;

import java.util.List;

/**
 * Author  :qujuncai
 * DATE    :18/11/26
 * Email   :qjchzq@163.com
 */
public class CoreActivity extends FragmentActivity {
    private static org.apache.logging.log4j.Logger logger = LoggerSetting.getLogger();
    private FragmentManager manager = null;
    private SetVmIdFragment setVmIdFragment = null;
    private MaintainFragment maintainFragment = null;
    private VmTypeFragment vmTypeFragment = null;

    private AbsFragment currentFragment = null;
    private boolean isOpenMModel = false;
    private Handler handler = new Handler();
    private Intent wachdogService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            service = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            service = IInstallInterface.Stub.asInterface(binder);
            logger.info("onServiceConnected={}",service);
        }
    };
    private IInstallInterface service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        manager = getSupportFragmentManager();
        registerIntentReceivers();
        wachdogService = new Intent(this, WatchDogService.class);
        startService(wachdogService);
        Intent intent = new Intent();
        intent.setPackage("com.kdx.install");
        intent.setAction(ActionConfig.S_ACTION_INSTALL);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Thread.setDefaultUncaughtExceptionHandler(new CoreExceptionHandler());
        logger.info("CoreActivity.onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logger.debug("CoreActivity.onResume");
        switchView();

    }
    private void registerIntentReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActionConfig.B_MODE_SWITCH);
        filter.setPriority(1000);
        registerReceiver(mReceiver, filter);
    }
    public void switchView(){
        boolean isInstall = false;
        try {
            String str_isInstall = PropertiesUtil.getPropertyValue(GlobalConfig.LOCALCONFIG_PATH,GlobalConfig.LOCAL_KEY_ISINSTALL);
            isInstall = TextUtils.equals(GlobalConfig.LOCAL_ISINSTALL_YES,str_isInstall);
        } catch (Exception e) {
            isInstall = false;
        }
        logger.debug("isInstall={},isOpenMModel={}",isInstall,isOpenMModel);
        if(isInstall){
            if(isOpenMModel){

                toMaintainView();
            }else{
                toLauncherView();
            }
        }else{
            toSetVmIdView();
        }
    }

    private void replaceFragment(AbsFragment fragment,String tag){
        logger.debug("replaceFragment:"+tag);
        currentFragment = fragment;
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.rl_content, fragment,tag);
        transaction.commitAllowingStateLoss();
    }

    public void toSetVmIdView() {
        setVmIdFragment = new SetVmIdFragment();
        replaceFragment(setVmIdFragment,setVmIdFragment.getTarget());
    }

    public void toMaintainView() {
        if(currentFragment==null || !TextUtils.equals(MaintainFragment.TARGET,currentFragment.getTarget())){
            maintainFragment = new MaintainFragment();
            replaceFragment(maintainFragment,maintainFragment.getTarget());
        }
    }

    public void toVmTypeView(){
        vmTypeFragment = new VmTypeFragment();
        replaceFragment(vmTypeFragment,vmTypeFragment.getTarget());
    }
    private void toLauncherView() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if(currentFragment != null){
                    rmFragment(currentFragment);
                }
                String startAction = CoreConfig.getLauncherAction();
                try {
                    Intent intent = new Intent(startAction);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.getInstanc(CoreActivity.this.getApplicationContext()).showToast("系统异常");
                }
            }
        }, 1000);
    }

    private void rmFragment(AbsFragment fragment){
        currentFragment = null;
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * am broadcast -a com.kdx.B_MODE_SWITCH --ei state 1
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            logger.info("mReceiver {}", action);
            if (TextUtils.equals(action,ActionConfig.B_MODE_SWITCH)) {
                boolean state = intent.getIntExtra("state", 0) == 1;
                if(isOpenMModel != state){
                    ActivityManager am = (ActivityManager) CoreActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(30);
                    for (ActivityManager.RunningTaskInfo taskInfo : taskList) {
                        String packageName = taskInfo.baseActivity.getPackageName();
                        if (packageName.equals(getPackageName())) {
                            am.moveTaskToFront(taskInfo.id,ActivityManager.MOVE_TASK_WITH_HOME);
                            break;
                        }
                    }
                    isOpenMModel = state;
                    switchView();
                }

            }
        }
    };

    public boolean install(String apkPath){
        if(service == null){
            Intent intent = new Intent();
            intent.setPackage("com.kdx.install");
            intent.setAction(ActionConfig.S_ACTION_INSTALL);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            return false;
        }else{
            try {
                return service.install(apkPath);
            } catch (Exception e) {
                return false;
            }
        }
    }
    public boolean unInstall(String apkPath){
        if(service == null){
            Intent intent = new Intent();
            intent.setPackage("com.kdx.install");
            intent.setAction(ActionConfig.S_ACTION_INSTALL);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            return false;
        }else{
            try {
                return service.uninstall(apkPath);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        logger.info("CoreActivity.onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logger.info("CoreActivity.onPause");
    }

    @Override
    protected void onDestroy() {
        logger.info("CoreActivity.onDestroy");
        super.onDestroy();
        unregisterReceiver(mReceiver);
        handler.removeCallbacksAndMessages(null);
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }
}
