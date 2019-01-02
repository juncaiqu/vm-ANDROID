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
 ** 发送给服务端，包含 注册 和客户端处理后的结果反馈
 */
public class Req2SMsg {


    /**
     * resultType : connect
     * content : null
     * innerCode : 123456
     * sessionId : null
     */

    private String resultType;
    private Object content;
    private String innerCode;
    private Object sessionId;

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public Object getSessionId() {
        return sessionId;
    }

    public void setSessionId(Object sessionId) {
        this.sessionId = sessionId;
    }
}
