package com.shuzicai.server.entry;

/**
 * Created by Nicky on 2017/2/21.
 * 记录游戏信息
 */
public class GameInfo {

    //objectId
    public final static String objectId_hushen = "vnGeVVVb";
    public final static String objectId_london = "YztiGGGg";
    //类型
    public final static int type_zhangdie = 0;
    public final static int type_weishu = 1;
    public final static int type_quanshu = 2;
    public final static int type_london = 3;
    //游戏类型0:涨跌，1猜尾数，2：猜全数,3:伦敦金
    private int gameType;
    //最新的期数
    private int newestNum;

    public GameInfo(int gameType, int newestNum) {
        this.gameType = gameType;
        this.newestNum = newestNum;
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
}
