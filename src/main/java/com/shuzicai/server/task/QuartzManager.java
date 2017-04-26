package com.shuzicai.server.task;

import com.shuzicai.server.task.job.GameIndexJob;
import com.shuzicai.server.task.job.GuessForecastJob;
import com.shuzicai.server.task.job.GuessMantissaJob;
import com.shuzicai.server.task.job.GuessWholeJob;
import com.shuzicai.server.task.job.LondonIndexJob;
import com.shuzicai.server.task.job.ShowIndexJob;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.HolidayCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * @author zz  2008-10-8 14:19:01
 * @version 1.00.000
 * @Title:Quartz管理类
 * @Description:
 * @Copyright:
 */
public class QuartzManager {
    //日志
    private static Logger log = LoggerFactory.getLogger(QuartzManager.class);

    //  星期一到星期五的15点35分0秒触发任务，股票任务
    private final static String StockIndexCronExpression = "0 35 15 ? * MON-FRI";
    //  星期一到星期五每10分钟运行一次,上午9:30-11:30,下午13:00-15:00
    private final static String GameIndexCronExpression1 = "0 35/10 9 ? * MON-FRI";
    private final static String GameIndexCronExpression2 = "0 5,15,25 11 ? * MON-FRI";
    private final static String GameIndexCronExpression3 = "0 5/10 10,13,14 ? * MON-FRI";
    //  星期一到星期五的0点0分0秒触发任务，伦敦金:00:00-06:00 07:00-23:59
    private final static String LondonIndexCronExpression1 = "0 5/10 0-5 ? * MON-FRI";
    private final static String LondonIndexCronExpression2 = "0 5/10 7-23 ? * MON-FRI";
    //涨跌与全数处理，每天9：00到15：59每分钟处理一次
    private final static String ForecastWholeExpression = "0 * 9-15 ? * MON-FRI";
    //尾数处理，每分钟处理一次
    private final static String ForecastMantissaExpression = "0 * * ? * MON-FRI";

    //测试
    private final static String TestExpression = "0 53 21 ? * MON-FRI";

    /**
     * 测试
     */
    public static void startTask() {
        log.info("\n=============start task==================\n");
        try {
            //每一天查一次就行,用于显示的股票信息
            createHuShenScheduler(ShowIndexJob.class, StockIndexCronExpression, "index");
            //各个时段
            createHuShenScheduler(GameIndexJob.class, GameIndexCronExpression1, "hushen1");
            createHuShenScheduler(GameIndexJob.class, GameIndexCronExpression2, "hushen2");
            createHuShenScheduler(GameIndexJob.class, GameIndexCronExpression3, "hushen3");
            //伦敦金
            createHuShenScheduler(LondonIndexJob.class, LondonIndexCronExpression1, "london1");
            createHuShenScheduler(LondonIndexJob.class, LondonIndexCronExpression2, "london2");
            //游戏预测结果处理
            createHuShenScheduler(GuessForecastJob.class, ForecastWholeExpression, "GuessForecastJob");
            createHuShenScheduler(GuessMantissaJob.class, ForecastMantissaExpression, "GuessMantissaJob");
            createHuShenScheduler(GuessWholeJob.class, ForecastWholeExpression, "GuessWholeJob");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取排除的日子
     *
     * @return
     */
    private static HolidayCalendar getRemoveDay() {
        // 国庆节
        Calendar cal = Calendar.getInstance();
        //5月份的假期
        cal.set(2017, Calendar.MAY, 29);
        cal.set(2017, Calendar.MAY, 30);
        //10月份的假期
        cal.set(2017, Calendar.OCTOBER, 2);
        cal.set(2017, Calendar.OCTOBER, 3);
        cal.set(2017, Calendar.OCTOBER, 4);
        cal.set(2017, Calendar.OCTOBER, 5);
        cal.set(2017, Calendar.OCTOBER, 6);

        // 排除国庆节的日期
        HolidayCalendar holidayCal = new HolidayCalendar();
        holidayCal.addExcludedDate(cal.getTime());
        return holidayCal;
    }

    /**
     * 创建定时器
     *
     * @throws SchedulerException
     */
    private static Scheduler createHuShenScheduler(Class<? extends Job> jobClass, String ctr, String flag) throws
            SchedulerException {
        log.info("\n\n=====创建定时器" + flag + "========\n");
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        //定义当前调度器的具体作业对象
        JobDetail jobDetail = JobBuilder.
                newJob(jobClass).
                withIdentity("cronTriggerDetail" + flag, "cronTriggerDetailGrounp" + flag).
                build();

        Trigger trigger3 = TriggerBuilder.newTrigger()
                .withIdentity("cron trigger" + flag, "createHuShenScheduler" + flag)
                .withSchedule(CronScheduleBuilder.cronSchedule(ctr)
                ).build();

        //排除这些日期
        scheduler.addCalendar("calendar" + flag, getRemoveDay(), true, false);
        scheduler.scheduleJob(jobDetail, trigger3);
        scheduler.start();
        return scheduler;
    }

    /**
     * 关闭定时器
     *
     * @throws SchedulerException
     */
    private static void stopScheduler(Scheduler scheduler) throws SchedulerException {
        if (null != scheduler) {
            if (scheduler.isStarted()) {
                scheduler.shutdown();
            }
        }
    }
}
