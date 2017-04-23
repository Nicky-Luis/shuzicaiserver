package com.shuzicai.server.task.job;

import com.shuzicai.server.service.GameLondonService;
import com.shuzicai.server.task.GussLondonJob;
import com.shuzicai.server.utils.DateUtils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Created by Nicky on 2017/4/8.
 * 任务
 */
public class LondonGoldJob implements Job {
    //日志
    private static Logger log = LoggerFactory.getLogger(LondonGoldJob.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        //作为显示的指数信息处理，每天请求一次就行
        GameLondonService.getLondonGoldValue();
        log.info("========获取伦敦金的数据=======");
        tryToStop();
    }

    /**
     * 尝试着停止
     */
    private void tryToStop() {
        //判断时间
        Calendar c = Calendar.getInstance();
        String currentTime = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        boolean isAM = DateUtils.isInTime("00:00-06:00", currentTime);
        boolean isPM = DateUtils.isInTime("07:00-23:59", currentTime);
        if (!isAM && !isPM) {
            log.info("不在时间范围之内,开始停止");
            try {
                GussLondonJob.stopScheduler();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        } else {
            log.info("在时间范围之内，继续运行..." + currentTime);
        }
    }

}