package com.kdx.core.net.protocal;

/**
 * Author  :qujuncai
 * DATE    :18/12/13
 * Email   :qjchzq@163.com
 */
public class BaseMsg {
    private String from;
    private String to;
    private long sn;


    public BaseMsg(String from, String to, long sn) {
        this.from = from;
        this.to = to;
        this.sn = sn;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getSn() {
        return sn;
    }

    public void setSn(long sn) {
        this.sn = sn;
    }

}
