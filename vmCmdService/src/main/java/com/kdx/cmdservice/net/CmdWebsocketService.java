package com.kdx.cmdservice.net;

import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;

import com.kdx.cmdservice.config.Config;
import com.kdx.cmdservice.handler.AbsHandler;
import com.kdx.cmdservice.handler.CmdlistHandler;
import com.kdx.cmdservice.handler.DownloadHandler;
import com.kdx.cmdservice.handler.InstallHandler;
import com.kdx.cmdservice.handler.ShellHandler;
import com.kdx.cmdservice.handler.StateHandler;
import com.kdx.cmdservice.handler.UnInstallHandler;
import com.kdx.cmdservice.handler.UnzipHandler;
import com.kdx.cmdservice.handler.UploadHandler;
import com.kdx.cmdservice.handler.VersionHandler;
import com.kdx.cmdservice.handler.ZipHandler;
import com.kdx.cmdservice.logger.LoggerSetting;
import com.kdx.cmdservice.model.Rep2CMsg;
import com.kdx.cmdservice.utils.JsonUtil;
import com.kdx.kdxutils.LocalThreadPoolExecutor;
import com.kdx.kdxutils.config.GlobalConfig;
import com.kdx.cmdservice.model.Req2SMsg;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by KDX on 2018/5/8.
 * websocket通讯管理器
 *
 * @author chengchuang
 */

public class CmdWebsocketService implements WebSocketListener {
    private static Logger logger = LoggerSetting.getLogger();
    private static CmdWebsocketService singleton;
    private WebSocket ws;
    private Map<String,AbsHandler> handlerMap = new HashMap<String, AbsHandler>();
    AtomicLong lastReceiverTime = new AtomicLong(0);
    private CmdWebsocketService() {
        AbsHandler cmdlistHandler = new CmdlistHandler();
        AbsHandler downloadHandler = new DownloadHandler();
        AbsHandler installHandler = new InstallHandler();
        AbsHandler shellHandler = new ShellHandler();
        AbsHandler stateHandler = new StateHandler();
        AbsHandler unInstallHandler = new UnInstallHandler();
        AbsHandler unzipHandler = new UnzipHandler();
        AbsHandler uploadHandler = new UploadHandler();
        AbsHandler versionHandler = new VersionHandler();
        AbsHandler zipHandler = new ZipHandler();
        handlerMap.put(cmdlistHandler.getMsgType(),cmdlistHandler);
        handlerMap.put(downloadHandler.getMsgType(),downloadHandler);
        handlerMap.put(installHandler.getMsgType(),installHandler);
        handlerMap.put(shellHandler.getMsgType(),shellHandler);
        handlerMap.put(stateHandler.getMsgType(),stateHandler);
        handlerMap.put(unInstallHandler.getMsgType(),unInstallHandler);
        handlerMap.put(unzipHandler.getMsgType(),unzipHandler);
        handlerMap.put(uploadHandler.getMsgType(),uploadHandler);
        handlerMap.put(versionHandler.getMsgType(),versionHandler);
        handlerMap.put(zipHandler.getMsgType(),zipHandler);
    }

    public static CmdWebsocketService getSingleton() {
        if (singleton == null) {
            synchronized (CmdWebsocketService.class) {
                if (singleton == null) {
                    singleton = new CmdWebsocketService();
                }
            }
        }


        return singleton;
    }


    public void connect(String serverUrl, String token) {
        try {
            if (null == ws) {
                ws = new WebSocketFactory().createSocket(serverUrl,60*1000);
                //ws.setPingInterval(5*60 * 1000);
                ws.addListener(this);
            } else {
                ws = ws.recreate();
            }
            ws.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (null != ws) {
            ws.disconnect();
            ws = null;
        }
    }

    public boolean isConnected() {
        return null != ws && ws.isOpen();
    }

    public void sendText(String content) {
        logger.info(content);
        if (null != ws && ws.isOpen()) {
            ws.sendText(content);
        } else {
        }
    }

    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        Req2SMsg reqMsg = new Req2SMsg();
        reqMsg.setResultType("connect");
        reqMsg.setContent(null);
        reqMsg.setInnerCode(Config.getVmId());
        reqMsg.setSessionId(null);
        sendText(JsonUtil.toJson(reqMsg));
        lastReceiverTime.set(System.currentTimeMillis());
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
        logger.info("onConnectError");
        cause.printStackTrace();
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        logger.info("onDisconnected");
    }

    @Override
    public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        logger.info("onCloseFrame");

    }

    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

        logger.info("onPingFrame");
    }

    @Override
    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

        logger.info("onPongFrame"+frame.toString());
        String str = new String(frame.getPayload(), 0, frame.getPayload().length, "UTF-8");
        logger.info("onPongFrame"+ str);
    }

    //接收消息
    @Override
    public void onTextMessage(WebSocket websocket, final String text) throws Exception {
        logger.info(text);
        lastReceiverTime.set(System.currentTimeMillis());
        LocalThreadPoolExecutor.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Rep2CMsg rep2CMsg = JsonUtil.jsonToObject(text, Rep2CMsg.class);
                    AbsHandler absHandler = handlerMap.get(rep2CMsg.getCmd());
                    logger.debug(absHandler);
                    if(null == absHandler){
                        absHandler = handlerMap.get("shell");
                    }
                    absHandler.handle(rep2CMsg);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error(e);
                }
            }
        });


    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {

    }

    @Override
    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
//        disconnect();

    }

    @Override
    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

    }

    @Override
    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

    }

    @Override
    public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
        logger.info("onThreadStopping");
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        logger.info("onError");
        cause.printStackTrace();
    }

    @Override
    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
        logger.info("onFrameError");
    }

    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
        logger.info("onMessageError");
    }

    @Override
    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
        logger.info("onMessageDecompressionError");
        cause.printStackTrace();
    }

    @Override
    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
        logger.info("onTextMessageError");
    }

    @Override
    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
        logger.info("onSendError");
    }

    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
        logger.info("onUnexpectedError");
        cause.printStackTrace();
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        logger.info("handleCallbackError");
        cause.printStackTrace();
    }

    @Override
    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
        logger.info("onSendingHandshake");

    }

    public Long getLastReceiverTime() {
        return lastReceiverTime.get();
    }
    public void setLastReceiverTime(long time) {
        lastReceiverTime.set(time);
    }
    public Map<String, AbsHandler> getHandlerMap() {
        return handlerMap;
    }
}
