package com.kdx.install;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kdx.kdxutils.ShellUtils;


/**
 * 工具类 暂时使用，等基础库再次完善后  再修改,避免乱引用 工具类名包含项目名
 */

public class InstallService extends Service {

    private static final String PERMISSION_STRING = "com.kdx.install.permission.ACCESS_INSTALL_SERVICE";

    public InstallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Install","onBind");
        return mbinder;
    }

    IInstallInterface.Stub mbinder = new IInstallInterface.Stub() {
        @Override
        public boolean install(String apkPath){
            return cmdExcu("pm install -r ",apkPath);
        }

        @Override
        public boolean uninstall(String apkPath){
            return cmdExcu("pm uninstall ",apkPath);
        }

        @Override
        public String execCmd(String cmd) throws RemoteException {
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand(cmd, true, true);
            Log.i("Install","resultInstall.errorMsg="+commandResult.errorMsg+",successMsg="+commandResult.successMsg+",result="+commandResult.result);
            if(commandResult.result == 0){
                return commandResult.successMsg;
            }else{
                return commandResult.errorMsg;
            }
        }
//        @Override
//        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
//            return checkCallingOrSelfPermission(PERMISSION_STRING) != PackageManager.PERMISSION_DENIED && super.onTransact(code, data, reply, flags);
//
//        }
    };

    private boolean cmdExcu(String cmd,String path)  {

        String installCmd = cmd+ path;
        Log.i("Install","cmd="+installCmd);
        ShellUtils.CommandResult resultInstall = ShellUtils.execCommand(installCmd, true, true);
        Log.i("Install","resultInstall.errorMsg="+resultInstall.errorMsg+",successMsg="+resultInstall.successMsg+",result="+resultInstall.result);
        return resultInstall.result == 0;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
