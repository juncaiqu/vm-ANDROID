package com.kdx.core.net.protocal.tovm;

import com.kdx.core.net.protocal.BaseMsg;

public class InnerCodeRpt  extends BaseMsg {
    private boolean needResend;
    private DataBean data;
    private int success;
    public InnerCodeRpt(String from, String to, long sn) {
        super(from, to, sn);
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

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public static class DataBean {
        /**
         * msgType : innerCodeReq
         * innerCode : 123
         * macAddress : cad3370665f44e929bb54b7ee7d82cc6
         */

        private String msgType;
        private String innerCode;
        private String macAddress;//md5加密
        private int vmType;

        public int getVmType() {
            return vmType;
        }

        public void setVmType(int vmType) {
            this.vmType = vmType;
        }

        public String getMsgType() {
            return msgType;
        }

        public void setMsgType(String msgType) {
            this.msgType = msgType;
        }

        public String getInnerCode() {
            return innerCode;
        }

        public void setInnerCode(String innerCode) {
            this.innerCode = innerCode;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }
    }

}