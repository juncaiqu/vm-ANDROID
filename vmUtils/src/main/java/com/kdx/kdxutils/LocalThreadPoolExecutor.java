package com.kdx.kdxutils;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class LocalThreadPoolExecutor extends ThreadPoolExecutor {

    private static LocalThreadPoolExecutor localThreadPoolExecutor = new LocalThreadPoolExecutor();

    public static LocalThreadPoolExecutor getInstance(){
        return localThreadPoolExecutor;
    }

    private LocalThreadPoolExecutor(){
        super(1, 30, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    public LocalThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if(t!=null){
            t.printStackTrace();
        }

        super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {


        super.terminated();
    }

}
