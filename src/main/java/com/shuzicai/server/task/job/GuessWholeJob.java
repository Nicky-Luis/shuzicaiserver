package com.shuzicai.server.task.job;

import com.shuzicai.server.service.GuessWholeService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nicky on 2017/4/8.
 * 任务
 */
public class GuessWholeJob implements Job {
    //日志
    private static Logger log = LoggerFactory.getLogger(GuessWholeJob.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        //猜全数结果处理
        GuessWholeService.startGuessWholeHandler();
        log.info("获取股票数据");
    }
}