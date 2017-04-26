package com.shuzicai.server.task.job;

import com.shuzicai.server.service.ShowIndexService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nicky on 2017/4/8.
 * 获取用于显示的股票信息
 */
public class ShowIndexJob implements Job {
    //日志
    private static Logger log = LoggerFactory.getLogger(ShowIndexJob.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        //作为显示的指数信息处理，每天请求一次就行
        log.info("\n============开始获取用于显示的股票数据，每天请求一次===========");
        ShowIndexService.getStockIndexDate();
    }
}