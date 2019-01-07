package com.kdx.filemanager.exception;


import java.lang.Thread.UncaughtExceptionHandler;


public class MyExceptionHandler implements UncaughtExceptionHandler {

    public MyExceptionHandler() {
    }
    
    /**
     * 处理异常，保存异常log或向服务器发送异常报告
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        System.exit(-1);
    }

}
