package com.kdx.cmdservice.handler;

import com.kdx.cmdservice.CmdApplication;
import com.kdx.cmdservice.config.Config;
import com.kdx.cmdservice.model.Rep2CMsg;
import com.kdx.cmdservice.model.Req2SMsg;
import com.kdx.cmdservice.net.CmdWebsocketService;
import com.kdx.cmdservice.utils.JsonUtil;
import com.kdx.cmdservice.utils.Util;

import java.util.List;
import java.util.Map;

/**
 * Author  :qujuncai
 * DATE    :18/12/27
 * Email   :qjchzq@163.com
 */
public class VersionHandler extends AbsHandler {
    @Override
    public void handle(Rep2CMsg rep2CMsg) {
        List<Map<String, String>> installAppInfo = Util.getInstallAppInfo(CmdApplication.getInstance());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < installAppInfo.size(); i++) {
            Map<String, String> map = installAppInfo.get(i);
            String appName = map.get("appName");
            String versionName = map.get("versionName");
            sb.append(appName).append(" : ").append(versionName).append("\r\n");
        }
        Req2SMsg reqMsg = new Req2SMsg();
        reqMsg.setResultType("");
        reqMsg.setContent( sb.toString());
        reqMsg.setInnerCode(Config.getVmId());
        reqMsg.setSessionId(rep2CMsg.getSessionId());
        CmdWebsocketService.getSingleton().sendText(JsonUtil.toJson(reqMsg));
    }

    @Override
    public String getMsgType() {
        return "version";
    }
}
