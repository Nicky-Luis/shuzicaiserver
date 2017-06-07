package com.shuzicai.server.task;

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
    //  星期一到星期五的没10分钟触发一次
    private final static String TaskCronExpression = "0 5/10 * ? * MON-FRI";
    //测试
    private final static String TestExpression = "0 5/10 * ? * MON-SUN";
    //Scheduler
    private static Scheduler scheduler;

    /**
     * 测试
     */
    public static void startTask() throws SchedulerException {
        scheduler = createHuShenScheduler(HandlerTaskJob.class,
                TaskCronExpression, "TaskCronExpression");
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
    private static Scheduler createHuShenScheduler(Class<? extends Job> jobClass,
                                                   String ctr,
                                                   String flag) throws
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
    private static void stopScheduler() throws SchedulerException {
        if (null != scheduler) {
            if (scheduler.isStarted()) {
                scheduler.shutdown();
            }
        }
    }
}
