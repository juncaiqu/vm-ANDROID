package com.kdx.cmdservice.handler;

import com.kdx.cmdservice.config.Config;
import com.kdx.cmdservice.logger.LoggerSetting;
import com.kdx.cmdservice.model.Rep2CMsg;
import com.kdx.cmdservice.model.Req2SMsg;
import com.kdx.cmdservice.net.CmdWebsocketService;
import com.kdx.cmdservice.utils.JsonUtil;

import org.apache.logging.log4j.Logger;

/**
 * Author  :qujuncai
 * DATE    :18/12/26
 * Email   :qjchzq@163.com
 */
public abstract class AbsHandler {
    protected static Logger logger = LoggerSetting.getLogger();
    public abstract void handle(Rep2CMsg rep2CMsg);
    public abstract String getMsgType();
    protected void sendReq2SMsg(String sessionId, String msg) {
        Req2SMsg reqMsg = new Req2SMsg();
        reqMsg.setResultType("");
        reqMsg.setContent(msg);
        reqMsg.setInnerCode(Config.getVmId());
        reqMsg.setSessionId(sessionId);
        CmdWebsocketService.getSingleton().sendText(JsonUtil.toJson(reqMsg));
    }
}
