package gp.e3.sentinel;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.jobs.CheckAllSystemsJob;
import gp.e3.sentinel.domain.repositories.RequestRepository;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.domain.repositories.UserRepository;
import gp.e3.sentinel.domain.workers.CheckSingleSystemWorker;
import gp.e3.sentinel.infrastructure.config.MySQLConfig;
import gp.e3.sentinel.infrastructure.config.RedisConfig;
import gp.e3.sentinel.infrastructure.healthchecks.MySQLHealthCheck;
import gp.e3.sentinel.infrastructure.healthchecks.RabbitMQHealthCheck;
import gp.e3.sentinel.infrastructure.healthchecks.RedisHealthCheck;
import gp.e3.sentinel.infrastructure.mq.RabbitHandler;
import gp.e3.sentinel.infrastructure.utils.JsonUtils;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;
import gp.e3.sentinel.persistence.daos.RequestDAO;
import gp.e3.sentinel.persistence.daos.SystemCacheDAO;
import gp.e3.sentinel.persistence.daos.SystemDAO;
import gp.e3.sentinel.persistence.daos.UserDAO;
import gp.e3.sentinel.service.SystemResource;
import gp.e3.sentinel.service.UserResource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.impl.client.HttpClientBuilder;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

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
		
		dataSource.setRemoveAbandonedTimeout(mySQLConfig.getRemoveAbandonedTimeoutInSeconds());
		dataSource.setRemoveAbandonedOnBorrow(mySQLConfig.isAbleToRemoveAbandonedConnections());
		dataSource.setRemoveAbandonedOnMaintenance(mySQLConfig.isAbleToRemoveAbandonedConnections());

		return dataSource;
	}

	private JedisPool getInitializedRedisPool(RedisConfig redisConfig) {

		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), redisConfig.getHost(), redisConfig.getPort(), 
				Protocol.DEFAULT_TIMEOUT, null, redisConfig.getSentinelDatabase());

		return jedisPool;
	}

	private void createTablesIfNeeded(java.sql.Connection dbConnection) {

		SystemDAO systemDAO = new SystemDAO();
		systemDAO.createSystemsTableIfDoesNotExist(dbConnection);

		RequestDAO requestDAO = new RequestDAO();
		requestDAO.createRequestsTableIfDoesNotExist(dbConnection);

		UserDAO userDAO = new UserDAO();
		userDAO.createUsersTablesIfNeeded(dbConnection);

		SqlUtils.closeDbConnection(dbConnection);
	}

	private UserRepository getUserRepository() {

		UserDAO userDAO = new UserDAO();
		return new UserRepository(userDAO);
	}

	private SystemRepository getSystemRepository(Gson gson) {

		SystemDAO systemDAO = new SystemDAO();
		SystemCacheDAO systemCacheDAO = new SystemCacheDAO(gson);
		return new SystemRepository(systemDAO, systemCacheDAO);
	}

	private RequestRepository getRequestRepository() {

		RequestDAO requestDAO = new RequestDAO();
		return new RequestRepository(requestDAO);
	}

	private void initializeCheckSingleSystemWorkers(int numberOfWorkers, BasicDataSource dataSource, JedisPool redisPool) {

		try {

			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(numberOfWorkers);

			Gson gson = JsonUtils.getDefaultGson();
			Connection rabbitConnection = RabbitHandler.getRabbitConnection(CheckAllSystemsJob.MQ_HOST);

			for (int i = 0; i < numberOfWorkers; i++) {

				UserRepository userRepository = getUserRepository();
				SystemRepository systemRepository = getSystemRepository(gson);
				RequestRepository requestRepository = getRequestRepository();
				fixedThreadPool.submit(new CheckSingleSystemWorker(gson, rabbitConnection, dataSource, redisPool, userRepository, 
						systemRepository, requestRepository, HttpClientBuilder.create()));
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private SystemBusiness getInitializedSystemBusiness(BasicDataSource dataSource) {

		Gson gson = JsonUtils.getDefaultGson();
		SystemRepository systemRepository = getSystemRepository(gson);

		return new SystemBusiness(dataSource, systemRepository);
	}

	private UserBusiness getInitializedUserBusiness(BasicDataSource dataSource) {

		UserDAO userDAO = new UserDAO();
		UserRepository userRepository = new UserRepository(userDAO);

		return new UserBusiness(dataSource, userRepository);
	}

	private void addMySQLHealthCheck(Environment environment, BasicDataSource dataSource) {

		try {
			environment.addHealthCheck(new MySQLHealthCheck("mysql", dataSource.getConnection()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addRabbitHealthCheck(Environment environment) {

		try {

			String host = "localhost";
			com.rabbitmq.client.Connection rabbitConnection = RabbitHandler.getRabbitConnection(host);
			environment.addHealthCheck(new RabbitMQHealthCheck("rabbit-mq", rabbitConnection));

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void addRedisHealthCheck(Environment environment, JedisPool redisPool) {

		environment.addHealthCheck(new RedisHealthCheck("redis", redisPool.getResource()));
	}

	@Override
	public void run(SentinelConfig configuration, Environment environment) throws Exception {

		BasicDataSource dataSource = getInitializedDataSource(configuration.getMySQLConfig());
		createTablesIfNeeded(dataSource.getConnection());

		int numberOfWorkers = 5;
		JedisPool redisPool = getInitializedRedisPool(configuration.getRedisConfig());
		initializeCheckSingleSystemWorkers(numberOfWorkers, dataSource, redisPool);

		SystemBusiness systemBusiness = getInitializedSystemBusiness(dataSource);
		systemBusiness.executeCheckAllSystemsJobForever();
		UserBusiness userBusiness = getInitializedUserBusiness(dataSource);

		// Add resources
		environment.addResource(new SystemResource(systemBusiness, userBusiness));
		environment.addResource(new UserResource(userBusiness));
		
		// Add health checks
		addMySQLHealthCheck(environment, dataSource);
		addRabbitHealthCheck(environment);
		addRedisHealthCheck(environment, redisPool);
	}
}