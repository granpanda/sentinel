package gp.e3.sentinel.domain.jobs;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.infrastructure.api_clients.EmailClient;
import gp.e3.sentinel.infrastructure.api_clients.SystemAPIClient;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckSystemExecutor implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSystemExecutor.class);

    private final int MAX_THREAD_POOL_SIZE = 10;

    private CheckSystemWorker getCheckSystemWorker(SystemBusiness systemBusiness, UserBusiness userBusiness,
                                                   int workerId, System system) {

        SystemAPIClient systemAPIClient = SystemAPIClient.getSystemAPIClient();
        EmailClient emailClient = new EmailClient();
        List<User> usersSubscribedToASystem = userBusiness.getAllUsersSubscribedToSystem(system.getId());

        return new CheckSystemWorker(workerId, systemAPIClient, emailClient, system, usersSubscribedToASystem, systemBusiness);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        SystemBusiness systemBusiness = (SystemBusiness) jobDataMap.get("systemBusiness");
        UserBusiness userBusiness = (UserBusiness) jobDataMap.get("userBusiness");

        List<gp.e3.sentinel.domain.entities.System> allSystems = systemBusiness.getAllSystems();
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_POOL_SIZE);

        for (int i = 0; i < allSystems.size(); i++) {

            int workerId = i + 1;
            System system = allSystems.get(i);
            CheckSystemWorker worker = getCheckSystemWorker(systemBusiness, userBusiness, workerId, system);
            executorService.submit(worker);
        }

        executorService.shutdown();
    }
}
