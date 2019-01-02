package com.kdx.core.net;


import android.os.SystemClock;
import android.text.TextUtils;

import com.kdx.core.config.CoreConfig;
import com.kdx.core.exception.KdxException;
import com.kdx.core.logger.LoggerSetting;
import com.kdx.kdxutils.LocalThreadPoolExecutor;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


public class SocketCommunication {
    private static org.apache.logging.log4j.Logger logger = LoggerSetting.getLogger();
    private CommunicationHandler communicationHandler;
    private static Object notifyObject = new Object();
    private Socket socketServer;
    private AtomicBoolean isConnected = new AtomicBoolean(false);
    private OutputStream serverOutputStream;
    private BufferedInputStream bin;
    private BufferedReader bufferedReader;
    private volatile AtomicLong lastReadTime = new AtomicLong(0);

    private static SocketCommunication socketCommunication = null;

    private SocketCommunication() {
    }

    public static SocketCommunication getSocketCommunication() {
        if (socketCommunication == null) {
            synchronized (SocketCommunication.class) {
                if (socketCommunication == null) {
                    socketCommunication = new SocketCommunication();
                }
            }
            return socketCommunication;
        } else {
            return socketCommunication;
        }

    }

    public void setCommunicationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    public void start() {
        LocalThreadPoolExecutor.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        });

    }

    private void connect() {
        synchronized (notifyObject){
            try {
                if(socketServer != null){
                    releaseSocket();
                    SystemClock.sleep(1000);
                }
                socketServer = new Socket();
                socketServer.connect(new InetSocketAddress(CoreConfig.getSocketUrl(), CoreConfig.getSocketPort()), 5000);
                socketServer.setKeepAlive(true);
                serverOutputStream = socketServer.getOutputStream();
                bin = new BufferedInputStream(socketServer.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(socketServer.getInputStream()));
                isConnected.set(true);
                lastReadTime.set(System.currentTimeMillis());
                communicationHandler.connected(this);
                while (isConnected.get()) {
                    String msg = bufferedReader.readLine();
                    if (TextUtils.isEmpty(msg)) {
                        Thread.sleep(200);
                        continue;
                    }
                    lastReadTime.set(System.currentTimeMillis());
                    communicationHandler.msgArrived(this,msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.releaseSocket();
            }
        }
    }

    public void releaseSocket() {

        try {
            isConnected.set(false);
            logger.info("socket releaseSocket..."+socketServer);
            if (socketServer != null && !socketServer.isClosed()) {

                communicationHandler.connectClosed();
                socketServer.shutdownInput();
                socketServer.shutdownOutput();
                socketServer.close();
                socketServer = null;
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void sendMsg(String msg) throws Exception {
        logger.info("send({}): {}",isConnected.get(),msg);
        if (!isConnected.get()) throw new KdxException("socket not connected");
        synchronized (this) {
            try {
                serverOutputStream.write(msg.getBytes(Charset.forName("utf-8")));
                serverOutputStream.write('\n');
                serverOutputStream.flush();
            } catch (Exception e) {
                logger.error(e);
                releaseSocket();
                throw e;
            }
        }
    }


}