package com.kdx.core.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import com.kdx.core.logger.LoggerSetting;
import com.kdx.kdxutils.ExceptionUtil;


public class CoreExceptionHandler implements UncaughtExceptionHandler {
    private static org.apache.logging.log4j.Logger logger = LoggerSetting.getLogger();

    public CoreExceptionHandler() {

    }
    
    /**
     * 处理异常，保存异常log或向服务器发送异常报告
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        logger.error(ExceptionUtil.errorConvert(ex));
        ex.printStackTrace();
        System.exit(-1);
    }

}
