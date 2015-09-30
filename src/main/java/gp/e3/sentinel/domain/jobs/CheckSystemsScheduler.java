package gp.e3.sentinel.domain.jobs;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.business.UserBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CheckSystemsScheduler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSystemsScheduler.class);

    private final SystemBusiness systemBusiness;
    private final UserBusiness userBusiness;

    public CheckSystemsScheduler(SystemBusiness systemBusiness, UserBusiness userBusiness) {
        this.systemBusiness = systemBusiness;
        this.userBusiness = userBusiness;
    }

    @Override
    public void run() {

        try {

            int initialDelay = 0;
            int delay = 60;
            ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            CheckSystemExecutor checkSystemExecutor = new CheckSystemExecutor(systemBusiness, userBusiness);
            scheduledExecutor.scheduleWithFixedDelay(checkSystemExecutor, initialDelay, delay, TimeUnit.SECONDS);

        } catch (Exception e) {

            LOGGER.error("run", e);
        }
    }
}
