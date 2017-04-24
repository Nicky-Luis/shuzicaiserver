package com.shuzicai.server.task;

import com.shuzicai.server.task.job.StockIndexJob;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
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
    //定时器
    private static Scheduler scheduler = null;

    //  星期一到星期五的15点15分0秒触发任务，股票任务
    private final static String StockIndexCronExpression = "0 15 15 ? * MON-FRI";
    //  星期一到星期五的9点35分0秒触发任务，上午的
    private final static String GameIndexCronExpression1 = "0 35 9 ? * MON-FRI";
    //  星期一到星期五的13点5分0秒触发任务，下午的
    private final static String GameIndexCronExpression2 = "0 5 13 ? * MON-FRI";
    //  星期一到星期五的0点0分0秒触发任务，伦敦金
    private final static String LondonIndexCronExpression1 = "0 0 0 ? * MON-FRI";
    //  星期一到星期五的7点0分0秒触发任务，伦敦金
    private final static String LondonIndexCronExpression2 = "0 0 7 ? * MON-FRI";
    //测试
    private final static String TestExpression = "0 35 22 ? * MON-FRI";

    //任务组
    private final static String JOB_GROUP_NAME = "QUARTZ_JOBGROUP_NAME";
    //触发器组
    private final static String TRIGGER_GROUP_NAME = "QUARTZ_TRIGGERGROUP_NAME";

    /**
     * 测试
     */
    public static void startTask() {
        log.info("\n=============start task==================\n");
        try {
            //每一天查一次就行,用于显示的股票信息
           createHuShenScheduler(StockIndexJob.class, StockIndexCronExpression, "index");
            //上午的 //下午的
            createHuShenScheduler(GussHuShenJob.class, GameIndexCronExpression1, "hushen1");
            createHuShenScheduler(GussHuShenJob.class, GameIndexCronExpression2, "hushen2");
            //伦敦金
            createHuShenScheduler(GussLondonJob.class, LondonIndexCronExpression1, "london1");
            createHuShenScheduler(GussLondonJob.class, LondonIndexCronExpression2, "london2");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加任务的方法
     *
     * @param jobName     任务名
     * @param triggerName 触发器名
     * @param jobClass    执行任务的类
     * @param seconds     间隔时间
     * @throws SchedulerException
     */
    public static Scheduler addJob(String jobName, String triggerName,
                                   Class<? extends Job> jobClass,
                                   int seconds) throws SchedulerException {
        log.info("==================initialization=================");
        //创建一个SchedulerFactory工厂实例
        SchedulerFactory sf = new StdSchedulerFactory();
        //通过SchedulerFactory构建Scheduler对象
        Scheduler sche = sf.getScheduler();
        log.info("===================initialize finshed===================");
        //用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, JOB_GROUP_NAME)
                .build();

        //构建一个触发器，规定触发的规则
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, TRIGGER_GROUP_NAME)//给触发器起一个名字和组名
                .startNow()//立即执行
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(seconds)//时间间隔  单位：秒
                        .repeatForever()).build();

        //向Scheduler中添加job任务和trigger触发器
        sche.scheduleJob(jobDetail, trigger);
        //启动
        sche.start();
        return sche;
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
    private static void createHuShenScheduler(Class<? extends Job> jobClass, String ctr, String flag) throws
            SchedulerException {
        log.info("\n\n=====创建定时器" + flag + "========\n");
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();

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
    }

    /**
     * 关闭定时器
     *
     * @throws SchedulerException
     */
    private static void stopScheduler() throws SchedulerException {
        if (null != scheduler) {
            if (scheduler.isStarted()) {
                scheduler.shutdown();
            }
            scheduler = null;
        }
    }
}
