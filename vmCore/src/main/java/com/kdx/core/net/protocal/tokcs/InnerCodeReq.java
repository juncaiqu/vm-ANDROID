package com.kdx.core.net.protocal.tokcs;

import com.kdx.core.net.protocal.BaseMsg;

/**
 * Author  :qujuncai
 * DATE    :18/12/13
 * Email   :qjchzq@163.com
 */
public class InnerCodeReq extends BaseMsg{
    private boolean needResend;
    private DataBean data;
    public InnerCodeReq(boolean needResend) {
        super("vmBox", "kcs", System.currentTimeMillis());
        this.needResend = needResend;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isNeedResend() {
        return needResend;
    }

    public void setNeedResend(boolean needResend) {
        this.needResend = needResend;
    }

    public static class DataBean {

        private String msgType = "innerCodeReq";
        private String macAddress;//md5加密

        public String getMsgType() {
            return msgType;
        }

        public void setMsgType(String msgType) {
            this.msgType = msgType;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }
    }
}
