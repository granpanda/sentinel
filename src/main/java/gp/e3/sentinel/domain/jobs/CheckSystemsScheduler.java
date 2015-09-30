package gp.e3.sentinel.domain.jobs;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.business.UserBusiness;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CheckSystemsScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSystemsScheduler.class);

    private final SystemBusiness systemBusiness;
    private final UserBusiness userBusiness;

    public CheckSystemsScheduler(SystemBusiness systemBusiness, UserBusiness userBusiness) {
        this.systemBusiness = systemBusiness;
        this.userBusiness = userBusiness;
    }

    public void executeJobForever(int secondsToRepeatTheJob) throws SchedulerException {

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("systemBusiness", systemBusiness);
        jobDataMap.put("userBusiness", userBusiness);

        JobDetail job = JobBuilder.newJob(CheckSystemExecutor.class)
                .setJobData(jobDataMap)
                .withIdentity("checkSystemsJobForever")
                .build();

        SimpleScheduleBuilder triggerScheduler = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(secondsToRepeatTheJob)
                .repeatForever();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("checkSystemsTriggerForever")
                .withSchedule(triggerScheduler)
                .startNow()
                .build();

        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }
}
