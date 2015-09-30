package gp.e3.sentinel.domain.jobs;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.infrastructure.api_clients.EmailClient;
import gp.e3.sentinel.infrastructure.api_clients.SystemAPIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckSystemExecutor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSystemExecutor.class);

    private final int MAX_THREAD_POOL_SIZE = 10;

    private final SystemBusiness systemBusiness;
    private final UserBusiness userBusiness;

    public CheckSystemExecutor(SystemBusiness systemBusiness, UserBusiness userBusiness) {
        this.systemBusiness = systemBusiness;
        this.userBusiness = userBusiness;
    }

    private CheckSystemWorker getCheckSystemWorker(int workerId, System system) {

        SystemAPIClient systemAPIClient = SystemAPIClient.getSystemAPIClient();
        EmailClient emailClient = new EmailClient();
        List<User> usersSubscribedToASystem = userBusiness.getAllUsersSubscribedToSystem(system.getId());

        return new CheckSystemWorker(workerId, systemAPIClient, emailClient, system, usersSubscribedToASystem, systemBusiness);
    }

    @Override
    public void run() {

        try {

            List<gp.e3.sentinel.domain.entities.System> allSystems = systemBusiness.getAllSystems();
            ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_POOL_SIZE);

            for (int i = 0; i < allSystems.size(); i++) {

                int workerId = i + 1;
                System system = allSystems.get(i);
                CheckSystemWorker worker = getCheckSystemWorker(workerId, system);
                executorService.submit(worker);
            }

            executorService.shutdown();

        } catch (Exception e) {

            LOGGER.error("run", e);
        }
    }
}
