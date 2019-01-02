package com.kdx.core.ov;

import android.support.annotation.NonNull;

/**
 * Author  :qujuncai
 * DATE    :18/12/11
 * Email   :qjchzq@163.com
 */
public class StateInfo implements Comparable<StateInfo>{
    private String stateName;
    private String stateValue;
    private int opType;

    public StateInfo(String stateName, String stateValue, int opType) {
        this.stateName = stateName;
        this.stateValue = stateValue;
        this.opType = opType;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateValue() {
        return stateValue;
    }

    public void setStateValue(String stateValue) {
        this.stateValue = stateValue;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    @Override
    public int compareTo(@NonNull StateInfo stateInfo) {
        return this.opType-stateInfo.getOpType();
    }
}
