package com.kdx.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import com.kdx.core.logger.LoggerSetting;


public class CoreExceptionHandler implements UncaughtExceptionHandler {
    private static org.apache.logging.log4j.Logger logger = LoggerSetting.getLogger();
	private UncaughtExceptionHandler defaultUEH;

    public CoreExceptionHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }
    
    /**
     * 处理异常，保存异常log或向服务器发送异常报告
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
       
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        String result = writer.toString();
        logger.error(result);
        System.exit(1);
        defaultUEH.uncaughtException(thread, ex);;
    }

}
