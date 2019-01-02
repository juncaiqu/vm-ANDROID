package com.kdx.cmdservice.handler;

import com.kdx.cmdservice.model.Rep2CMsg;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class InstallHandler extends AbsHandler {
    @Override
    public void handle(Rep2CMsg rep2CMsg) {

    }

    @Override
    public String getMsgType() {
        return "install";
    }
}
