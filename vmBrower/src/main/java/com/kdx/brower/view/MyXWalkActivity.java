package com.kdx.brower.view;

import org.apache.logging.log4j.Logger;
import org.xwalk.core.MyXWalkActivityDelegate;

import com.kdx.brower.logger.LoggerSetting;
import com.kdx.brower.utils.XWalkUtil;

import android.app.Activity;
import android.os.Bundle;

public abstract class MyXWalkActivity extends Activity{
    private static Logger logger = LoggerSetting.getLogger();
	private MyXWalkActivityDelegate mActivityDelegate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCrossWalkEnv();
    }

    public void initCrossWalkEnv() {
        //XWalkUtil.delSo(this.getApplicationContext());
        onXWalkCoreReady();
    }
    
    protected void onXWalkCoreReady() { 
        mActivityDelegate = new MyXWalkActivityDelegate(this, cancelCommand, completeCommand);
    }
    
    /**
     * 加载cross内核
     */
    public abstract void loadCrosswalk();

    public abstract void onXWalkReady() ;

    @Override
    protected void onResume() {
        super.onResume();
        if(mActivityDelegate != null){
            mActivityDelegate.onResume();
        }
    }
    
    

    Runnable cancelCommand = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };
    Runnable completeCommand = new Runnable() {
        @Override
        public void run() {
            onXWalkReady();
        }
    };
    
}
