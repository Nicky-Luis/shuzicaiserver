package com.shuzicai.server.entry;

/**
 * Created by Nicky on 2017/2/21.
 * 记录游戏信息
 */
public class GameInfo {
    //游戏类型0:涨跌，1猜尾数，2：猜全数
    private int gameType;
    //最新的期数
    private int newestNum;
    //objectId
    private String objectId;

    public GameInfo(int gameType, int newestNum, String objectId) {
        this.gameType = gameType;
        this.newestNum = newestNum;
        this.objectId = objectId;
    }

    public GameInfo() {
    }

    public int getGameType() {
        return gameType;
    }

    public int getNewestNum() {
        return newestNum;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public void setNewestNum(int newestNum) {
        this.newestNum = newestNum;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }
}
