package com.shuzicai.server.task;

import com.shuzicai.server.service.GameLondonService;
import com.shuzicai.server.service.ShowIndexService;
import com.shuzicai.server.utils.DateUtils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Created by Nicky on 2017/4/8.
 * 获取沪深300 股票信息,用于游戏预测
 */
public class HandlerTaskJob implements Job {
    //日志
    private static Logger log = LoggerFactory.getLogger(HandlerTaskJob.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        londonHandler();
        stockIndexHandler();
    }


    private void londonHandler() {
        log.info("\n\n===========获取伦敦金的数据,每10分钟一次=======");
        //判断时间
        Calendar c = Calendar.getInstance();
        String currentTime = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        //boolean isAM = DateUtils.isInTime("00:00-04:00", currentTime);
        boolean isPM = DateUtils.isInTime("09:00-23:59", currentTime);
        if (/*isAM ||*/ isPM) {
            GameLondonService.getLondonGoldValue();
        } else {
            log.info("\n=====伦敦金操作，不在时间范围之内=======");
        }
    }

    private void stockIndexHandler() {
        log.info("\n============开始获取用于显示的股票数据，每天请求一次===========");
        //判断时间
        Calendar c = Calendar.getInstance();
        String currentTime = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        boolean isBetween = DateUtils.isInTime("15:30-15:40", currentTime);
        if (isBetween) {
            ShowIndexService.getStockIndexDate();
        } else {
            log.info("\n=====用于显示的指数操作，不在时间范围之内=======");
        }

    }

}