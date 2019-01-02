package com.kdx.cmdservice.model;

/**
 * Created by KDX on 2018/5/8.
 *
 * 通讯协议：websocket
 url：ws://vd.touchfound.net:8181
 客户端连接上是发送：
 {"resultType":"connect","content":null,"innerCode":"123456","sessionId":null}
 客户端收到服务器端下发的命令格式：
 {"paramString":"","cmd":"dir","sessionId":"d0i4mcscnirfnshjxjk53tfr"}
 客户端处理成功之后需要返回：
 {"resultType":"","content":"客户端返回结果","innerCode":"123456","sessionId":"d0i4mcscnirfnshjxjk53tfr"}
 *
 *
 *
 * 接收的来自服务端的消息
 */

public class Rep2CMsg {


    /**
     * paramString :
     * cmd : dir
     * sessionId : d0i4mcscnirfnshjxjk53tfr
     */

    private String paramString;
    private String cmd;
    private String sessionId;

    public String getParamString() {
        return paramString;
    }

    public void setParamString(String paramString) {
        this.paramString = paramString;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Rep2CMsg{" +
                "paramString='" + paramString + '\'' +
                ", cmd='" + cmd + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
