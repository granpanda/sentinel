package gp.e3.sentinel;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.jobs.CheckAllSystemsJob;
import gp.e3.sentinel.domain.repositories.RequestRepository;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.domain.workers.CheckSingleSystemWorker;
import gp.e3.sentinel.infrastructure.MySQLConfig;
import gp.e3.sentinel.infrastructure.mq.RabbitHandler;
import gp.e3.sentinel.infrastructure.utils.JsonUtils;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;
import gp.e3.sentinel.persistence.daos.RequestDAO;
import gp.e3.sentinel.persistence.daos.SystemDAO;
import gp.e3.sentinel.service.SystemResource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class Sentinel extends Service<SentinelConfig> {

	public static void main(String[] args) {

		try {

			Sentinel sentinel = new Sentinel();
			sentinel.run(args);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public void initialize(Bootstrap<SentinelConfig> bootstrap) {

	}

	private BasicDataSource getInitializedDataSource(MySQLConfig mySQLConfig) {

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(mySQLConfig.getDriverClass());
		dataSource.setUrl(mySQLConfig.getUrl());
		dataSource.setUsername(mySQLConfig.getUsername());
		dataSource.setPassword(mySQLConfig.getPassword());

		return dataSource;
	}

	private void createTablesIfNeeded(java.sql.Connection dbConnection) {

		SystemDAO systemDAO = new SystemDAO();
		systemDAO.createSystemsTableIfDoesNotExist(dbConnection);

		RequestDAO requestDAO = new RequestDAO();
		requestDAO.createRequestsTableIfDoesNotExist(dbConnection);

		SqlUtils.closeDbConnection(dbConnection);
	}

	private RequestRepository getRequestRepository() {

		RequestDAO requestDAO = new RequestDAO();
		return new RequestRepository(requestDAO);
	}

	private SystemBusiness getInitializedSystemBusiness(BasicDataSource dataSource) {

		SystemDAO systemDAO = new SystemDAO();
		SystemRepository systemRepository = new SystemRepository(systemDAO);

		return new SystemBusiness(dataSource, systemRepository);
	}

	private void initializeCheckSingleSystemWorkers(int numberOfWorkers, BasicDataSource dataSource) {

		try {

			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(numberOfWorkers);

			Gson gson = JsonUtils.getDefaultGson();
			Connection rabbitConnection = RabbitHandler.getRabbitConnection(CheckAllSystemsJob.MQ_HOST);

			for (int i = 0; i < numberOfWorkers; i++) {

				RequestRepository requestRepository = getRequestRepository();
				fixedThreadPool.submit(new CheckSingleSystemWorker(gson, rabbitConnection, dataSource, requestRepository, HttpClientBuilder.create()));
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void run(SentinelConfig configuration, Environment environment) throws Exception {

		BasicDataSource dataSource = getInitializedDataSource(configuration.getMySQLConfig());
		createTablesIfNeeded(dataSource.getConnection());

		int numberOfWorkers = 5;
		initializeCheckSingleSystemWorkers(numberOfWorkers, dataSource);

		SystemBusiness systemBusiness = getInitializedSystemBusiness(dataSource);
		systemBusiness.executeCheckAllSystemsJobForever();

		environment.addResource(new SystemResource(systemBusiness));
	}
}