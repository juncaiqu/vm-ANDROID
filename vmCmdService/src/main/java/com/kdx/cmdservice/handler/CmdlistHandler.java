package com.kdx.cmdservice.handler;

import com.kdx.cmdservice.model.Rep2CMsg;
import com.kdx.cmdservice.net.CmdWebsocketService;

import java.util.Map;
import java.util.Set;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class CmdlistHandler extends AbsHandler {
    @Override
    public void handle(Rep2CMsg rep2CMsg) {
        Map<String, AbsHandler> handlerMap = CmdWebsocketService.getSingleton().getHandlerMap();
        Set<String> cmds = handlerMap.keySet();
        StringBuffer sb = new StringBuffer();
        for (String cmd:cmds) {
            sb.append(cmd);
            sb.append("   ");
        }
        sendReq2SMsg(rep2CMsg.getSessionId(), sb.toString());
    }

    @Override
    public String getMsgType() {
        return "cmdlist";
    }
}
