package gp.e3.sentinel.domain.jobs;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.infrastructure.api_clients.EmailClient;
import gp.e3.sentinel.infrastructure.api_clients.SystemAPIClient;
import gp.e3.sentinel.infrastructure.utils.HttpUtils;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CheckSystemWorker implements Runnable {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CheckSystemWorker.class);

    private final int workerId;
    private final SystemAPIClient systemAPIClient;
    private final EmailClient emailClient;

    private final gp.e3.sentinel.domain.entities.System system;
    private final List<User> systemUsers;

    private final SystemBusiness systemBusiness;

    public CheckSystemWorker(int workerId, SystemAPIClient systemAPIClient, EmailClient emailClient,
                             System system, List<User> systemUsers, SystemBusiness systemBusiness) {
        this.workerId = workerId;
        this.systemAPIClient = systemAPIClient;
        this.emailClient = emailClient;
        this.system = system;
        this.systemUsers = systemUsers;
        this.systemBusiness = systemBusiness;
    }

    @Override
    public void run() {

        try {

            Request request = systemAPIClient.checkSystemIsAlive(workerId, system);
            int statusCode = request.getHttpResponseStatusCode();
            long systemId = system.getId();

            if (!HttpUtils.isSuccessfulRequest(statusCode)) {

                if (!systemBusiness.isSystemInCache(systemId)) {

                    emailClient.sendEmail(request, systemUsers);
                    systemBusiness.addSystemToCache(system);
                }

            } else {

                systemBusiness.deleteSystemFromCache(systemId);
            }

        } catch (Exception e) {

            LOGGER.error("run", e);
        }
    }
}
