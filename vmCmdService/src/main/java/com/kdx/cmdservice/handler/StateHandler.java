package com.kdx.cmdservice.handler;

import com.kdx.cmdservice.CmdApplication;
import com.kdx.cmdservice.model.Rep2CMsg;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class StateHandler extends AbsHandler {
    @Override
    public void handle(Rep2CMsg rep2CMsg) {
        sendReq2SMsg(rep2CMsg.getSessionId(), CmdApplication.getInstance().getStatusInfo());
    }

    @Override
    public String getMsgType() {
        return "state";
    }
}
