package com.kdx.core.net.protocal.tokcs;

import com.kdx.core.net.protocal.BaseMsg;

import java.io.Serializable;

public class Ack extends BaseMsg implements Serializable {


    private static final long serialVersionUID = 5683563881511450082L;
    private int success;

    public Ack(long sn) {
        super("vmBox", "kcs", sn);
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}