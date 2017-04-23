package com.shuzicai.server.entry;

/**
 * Created by Nicky on 2017/2/19.
 * 尾数预测记录
 */

public class GuessMantissaRecord {

    //游戏类型
    public final static int Guess_Type_Percentile = 0;//百分位直选
    public final static int Guess_Type_DoubleDirect = 1;//双数直选
    public final static int Guess_Type_DoubleGroup = 2;//双数组选
    //指数类型
    public final static int Index_Type_Hushen = 0;
    public final static int Index_Type_Gold = 1;
    //objectID
    private String objectId;
    //用户id
    private String userId;
    //押注金币数值
    private float goldValue;
    //类型
    private int guessType;
    //指数类型,黄金或者沪深300
    private int indexType;
    //押注期数
    private int periodNum;
    //押注的数据
    private int guessValue;
    //实际的指数
    private float indexResult;
    //是否中奖
    private boolean isReward;
    //获取的奖励数量
    private float rewardCount;
    //奖励是否已经同步：0：未同步，1：已经同步
    private int rewardFlag;
    //预测是否已经处理：0：未处理，1：已经处理
    private int handlerFlag;

    public GuessMantissaRecord(String objectId, String userId, float goldValue, int guessType, int indexType, int periodNum, int guessValue, float indexResult, boolean isReward, float rewardCount, int rewardFlag, int handlerFlag) {
        this.objectId = objectId;
        this.userId = userId;
        this.goldValue = goldValue;
        this.guessType = guessType;
        this.indexType = indexType;
        this.periodNum = periodNum;
        this.guessValue = guessValue;
        this.indexResult = indexResult;
        this.isReward = isReward;
        this.rewardCount = rewardCount;
        this.rewardFlag = rewardFlag;
        this.handlerFlag = handlerFlag;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getUserId() {
        return userId;
    }

    public float getGoldValue() {
        return goldValue;
    }

    public int getGuessType() {
        return guessType;
    }

    public int getIndexType() {
        return indexType;
    }

    public int getPeriodNum() {
        return periodNum;
    }

    public int getGuessValue() {
        return guessValue;
    }

    public float getIndexResult() {
        return indexResult;
    }

    public boolean isReward() {
        return isReward;
    }

    public float getRewardCount() {
        return rewardCount;
    }

    public int getRewardFlag() {
        return rewardFlag;
    }

    public int getHandlerFlag() {
        return handlerFlag;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGoldValue(float goldValue) {
        this.goldValue = goldValue;
    }

    public void setGuessType(int guessType) {
        this.guessType = guessType;
    }

    public void setIndexType(int indexType) {
        this.indexType = indexType;
    }

    public void setPeriodNum(int periodNum) {
        this.periodNum = periodNum;
    }

    public void setGuessValue(int guessValue) {
        this.guessValue = guessValue;
    }

    public void setIndexResult(float indexResult) {
        this.indexResult = indexResult;
    }

    public void setReward(boolean reward) {
        isReward = reward;
    }

    public void setRewardCount(float rewardCount) {
        this.rewardCount = rewardCount;
    }

    public void setRewardFlag(int rewardFlag) {
        this.rewardFlag = rewardFlag;
    }

    public void setHandlerFlag(int handlerFlag) {
        this.handlerFlag = handlerFlag;
    }
}
