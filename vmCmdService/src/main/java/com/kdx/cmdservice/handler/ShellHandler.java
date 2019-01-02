package com.kdx.cmdservice.handler;

import com.kdx.cmdservice.model.Rep2CMsg;
import com.kdx.kdxutils.ShellUtils;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class ShellHandler extends AbsHandler {
    @Override
    public void handle(Rep2CMsg rep2CMsg) {
        String shellCmd = rep2CMsg.getCmd() + " " + rep2CMsg.getParamString();
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(shellCmd, true, true);
        sendReq2SMsg(rep2CMsg.getSessionId(), commandResult.successMsg+" "+commandResult.errorMsg);
    }

    @Override
    public String getMsgType() {
        return "shell";
    }
}
