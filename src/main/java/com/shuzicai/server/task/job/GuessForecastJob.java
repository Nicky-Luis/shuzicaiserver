package com.shuzicai.server.task.job;

import com.shuzicai.server.service.GuessForecastService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nicky on 2017/4/8.
 * 任务
 */
public class GuessForecastJob implements Job {
    //日志
    private static Logger log = LoggerFactory.getLogger(GuessForecastJob.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.info("\n\n===============开始涨跌预测游戏处理================\n");
        GuessForecastService.startForecastHandler();
    }
}