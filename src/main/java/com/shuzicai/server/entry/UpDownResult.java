package com.shuzicai.server.entry;

/**
 * Created by Nicky on 2017/2/21.
 * 涨跌期数结果
 */
public class UpDownResult {

    //期数
    private int periodNum;
    //实际结果
    private float resultIndex;

    public UpDownResult(int periodNum, float resultIndex) {
        this.periodNum = periodNum;
        this.resultIndex = resultIndex;
    }

    public UpDownResult() {
    }

    public int getPeriodNum() {
        return periodNum;
    }

    public float getResultIndex() {
        return resultIndex;
    }

    public void setPeriodNum(int periodNum) {
        this.periodNum = periodNum;
    }

    public void setResultIndex(float resultIndex) {
        this.resultIndex = resultIndex;
    }
}
