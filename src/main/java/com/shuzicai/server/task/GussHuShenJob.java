package com.shuzicai.server.task;

import com.shuzicai.server.task.job.GameIndexJob;
import com.shuzicai.server.task.job.GuessForecastJob;
import com.shuzicai.server.task.job.GuessMantissaJob;
import com.shuzicai.server.task.job.GuessWholeJob;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nicky on 2017/4/11.
 * 沪深300的游戏任务
 */
public class GussHuShenJob implements Job {
    //日志
    private static Logger log = LoggerFactory.getLogger(GameIndexJob.class);
    //主Scheduler
    private static Scheduler mainScheduler;
    private static Scheduler mainScheduler1, mainScheduler2, mainScheduler3;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.info("\n==========获取股票数据============\n");
        try {
            if (null == mainScheduler) {
                //添加股票查询任务,每10分钟一次
                mainScheduler = QuartzManager.addJob("job1", "trigger1",
                        GameIndexJob.class, 60 * 10);
            }

            //每1分钟进行游戏结果计算
            if (null == mainScheduler1) {
                mainScheduler1 = QuartzManager.addJob("Job2", "trigger2",
                        GuessForecastJob.class, 60);
            }
            if (null == mainScheduler2) {
                mainScheduler2 = QuartzManager.addJob("Job3", "trigger3",
                        GuessMantissaJob.class, 60);
            }
            if (null == mainScheduler3) {
                mainScheduler3 = QuartzManager.addJob("Job4", "trigger4",
                        GuessWholeJob.class, 60);
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
        log.info("=====时间已到停止本周期的更新=====");
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
        if (null != mainScheduler2) {
            if (mainScheduler2.isStarted()) {
                mainScheduler2.shutdown();
            }
            mainScheduler2 = null;
        }
        if (null != mainScheduler3) {
            if (mainScheduler3.isStarted()) {
                mainScheduler3.shutdown();
            }
            mainScheduler3 = null;
        }
    }
}
