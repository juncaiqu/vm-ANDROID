package com.kdx.cmdservice;

import android.content.Context;
import android.os.SystemClock;

import com.kdx.cmdservice.config.Config;
import com.kdx.cmdservice.logger.LoggerSetting;
import com.kdx.cmdservice.net.CmdWebsocketService;

import org.apache.logging.log4j.Logger;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class CmdMain {
    private static Logger logger = LoggerSetting.getLogger();
    private boolean isClose = false;
    private static CmdMain singleton;
    private CmdMain() {
    }

    public static CmdMain getSingleton() {
        if (singleton == null) {
            synchronized (CmdMain.class) {
                if (singleton == null) {
                    singleton = new CmdMain();
                }
            }
        }

        return singleton;
    }

    public void start(Context context){
        isClose = false;
        while (!isClose){
            try {
                CmdWebsocketService cmdWebsocketService = CmdWebsocketService.getSingleton();
                cmdWebsocketService.connect(Config.getWebsocketServerUrl(),"");
                while (!isClose){

                    SystemClock.sleep(1000*20);
                    if(System.currentTimeMillis() - cmdWebsocketService.getLastReceiverTime() >= (1000*60)){
                        //cmdWebsocketService.setLastReceiverTime(System.currentTimeMillis());
                        cmdWebsocketService.disconnect();
                    }
                    break;
                    /*if(!cmdWebsocketService.isConnected()){
                        cmdWebsocketService.disconnect();
                        break;
                    }*/
                }
                SystemClock.sleep(1000*40);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
    public void close(){
        isClose = true;
        CmdWebsocketService.getSingleton().disconnect();
    }
}
