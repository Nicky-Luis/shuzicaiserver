package com.shuzicai.server.entry;

import java.util.Date;

/**
 * Created by Nicky on 2017/2/21.
 */
public class StockIndex1 {
    private float yestodayClosePrice;
    private float todayMax;
    private float todayMin;
    private float max52;
    //变化值
    private float diff_money;
    //交易量
    private float tradeNum;
    //股票代码
    private String code;
    //最大价格
    private float maxPrice;
    //当前价格
    private float nowPrice;
    private float min52;
    private Date time;
    private String name;
    //交易总额
    private double tradeAmount;
    //摇荡
    private float swing;
    private float todayOpenPrice;
    private float diff_rate;
    private float minPrice;

    public StockIndex1(float yestodayClosePrice, float todayMax, float todayMin, float max52, float diff_money, float
            tradeNum, String code, float maxPrice, float nowPrice, float min52, Date time, String name, double
            tradeAmount, float swing, float todayOpenPrice, float diff_rate, float minPrice) {
        this.yestodayClosePrice = yestodayClosePrice;
        this.todayMax = todayMax;
        this.todayMin = todayMin;
        this.max52 = max52;
        this.diff_money = diff_money;
        this.tradeNum = tradeNum;
        this.code = code;
        this.maxPrice = maxPrice;
        this.nowPrice = nowPrice;
        this.min52 = min52;
        this.time = time;
        this.name = name;
        this.tradeAmount = tradeAmount;
        this.swing = swing;
        this.todayOpenPrice = todayOpenPrice;
        this.diff_rate = diff_rate;
        this.minPrice = minPrice;
    }

    public StockIndex1() {
    }

}
