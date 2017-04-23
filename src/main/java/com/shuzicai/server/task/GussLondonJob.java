package com.shuzicai.server.task;

import com.shuzicai.server.task.job.GameIndexJob;
import com.shuzicai.server.task.job.GuessMantissaJob;
import com.shuzicai.server.task.job.LondonGoldJob;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nicky on 2017/4/11.
 * 伦敦金的任务
 */
public class GussLondonJob implements Job {
    //日志
    private static Logger log = LoggerFactory.getLogger(GameIndexJob.class);
    //主Scheduler
    private static Scheduler mainScheduler, mainScheduler1;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.info("=====获取伦敦金数据=====");
        try {
            //伦敦金任务，每10分钟一次
            if (null == mainScheduler) {
                mainScheduler = QuartzManager.addJob("GussLondonJob",
                        "GussLondonTrigger", LondonGoldJob.class, 60 * 10);
            }
            //每1分钟进行游戏结果计算
            if (null == mainScheduler1) {
                mainScheduler1 = QuartzManager.addJob("GussLondonJob1",
                        "GussLondonTrigger2", GuessMantissaJob.class, 60);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭
     *
     * @throws SchedulerException
     */
    public static void stopScheduler() throws SchedulerException {
        if (null != mainScheduler) {
            if (mainScheduler.isStarted()) {
                mainScheduler.shutdown();
            }
            mainScheduler = null;
        }
        if (null != mainScheduler1) {
            if (mainScheduler1.isStarted()) {
                mainScheduler1.shutdown();
            }
            mainScheduler1 = null;
        }
    }
}
