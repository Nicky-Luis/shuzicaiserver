package com.shuzicai.server.task.job;

import com.shuzicai.server.service.GameIndexService;
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
public class GameIndexJob implements Job {
    //日志
    private static Logger log = LoggerFactory.getLogger(GameIndexJob.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.info("\n\n===============获取沪深300游戏信息，每10分钟请求一次================\n");
        GameIndexService.getStockIndexDate();
        tryToStop();
    }

    //不在时间范围内就停止
    private void tryToStop() {
        //判断时间
        Calendar c = Calendar.getInstance();
        String currentTime = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        boolean isAM = DateUtils.isInTime("09:30-11:30", currentTime);
        boolean isPM = DateUtils.isInTime("13:00-15:00", currentTime);
        if (!isAM && !isPM) {
            log.info("不在时间范围之内");
        } else {
            log.info("在时间范围之内，继续运行..." + currentTime);
        }
    }

}