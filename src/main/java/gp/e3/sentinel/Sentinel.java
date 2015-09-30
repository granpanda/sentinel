package gp.e3.sentinel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.gson.Gson;
import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.jobs.CheckSystemsScheduler;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.domain.repositories.UserRepository;
import gp.e3.sentinel.infrastructure.config.MySQLConfig;
import gp.e3.sentinel.infrastructure.config.RedisConfig;
import gp.e3.sentinel.infrastructure.healthchecks.MySQLHealthCheck;
import gp.e3.sentinel.infrastructure.healthchecks.RedisHealthCheck;
import gp.e3.sentinel.infrastructure.utils.JsonUtils;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;
import gp.e3.sentinel.persistence.daos.RequestDAO;
import gp.e3.sentinel.persistence.daos.SystemCacheDAO;
import gp.e3.sentinel.persistence.daos.SystemDAO;
import gp.e3.sentinel.persistence.daos.UserDAO;
import gp.e3.sentinel.service.SystemResource;
import gp.e3.sentinel.service.UserResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.commons.dbcp2.BasicDataSource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.sql.SQLException;

public class Sentinel extends Application<SentinelConfig> {

	@Override
	public void initialize(Bootstrap<SentinelConfig> bootstrap) {

	}

	public static ObjectMapper configureJackson(ObjectMapper objectMapper) {

		objectMapper.registerModule(new JodaModule());
		objectMapper.setDateFormat(new ISO8601DateFormat());
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper;
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

	private SystemRepository getSystemRepository(Gson gson) {

		SystemDAO systemDAO = new SystemDAO();
		SystemCacheDAO systemCacheDAO = new SystemCacheDAO(gson);
		return new SystemRepository(systemDAO, systemCacheDAO);
	}

	private SystemBusiness getInitializedSystemBusiness(JedisPool redisPool, BasicDataSource dataSource) {

		Gson gson = JsonUtils.getDefaultGson();
		SystemRepository systemRepository = getSystemRepository(gson);
		return new SystemBusiness(redisPool, dataSource, systemRepository);
	}

	private UserBusiness getInitializedUserBusiness(BasicDataSource dataSource) {

		UserDAO userDAO = new UserDAO();
		UserRepository userRepository = new UserRepository(userDAO);
		return new UserBusiness(dataSource, userRepository);
	}

	private void addMySQLHealthCheck(Environment environment, BasicDataSource dataSource) {

		try {
			MySQLHealthCheck mySqlHealthCheck = new MySQLHealthCheck(dataSource.getConnection());
			environment.healthChecks().register("mysql", mySqlHealthCheck);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addRedisHealthCheck(Environment environment, JedisPool redisPool) {

		RedisHealthCheck redisHealthCheck = new RedisHealthCheck(redisPool.getResource());
		environment.healthChecks().register("redis", redisHealthCheck);
	}

	@Override
	public void run(SentinelConfig configuration, Environment environment) throws Exception {

		BasicDataSource dataSource = getInitializedDataSource(configuration.getMySQLConfig());
		createTablesIfNeeded(dataSource.getConnection());

		JedisPool redisPool = getInitializedRedisPool(configuration.getRedisConfig());
		SystemBusiness systemBusiness = getInitializedSystemBusiness(redisPool, dataSource);
		UserBusiness userBusiness = getInitializedUserBusiness(dataSource);

		// Add resources
		environment.jersey().register(new SystemResource(systemBusiness, userBusiness));
		environment.jersey().register(new UserResource(userBusiness));
		
		// Add health checks
		addMySQLHealthCheck(environment, dataSource);
		addRedisHealthCheck(environment, redisPool);

		// Start check systems scheduler
		CheckSystemsScheduler scheduler = new CheckSystemsScheduler(systemBusiness, userBusiness);
		scheduler.run();
	}

	public static void main(String[] args) {

		try {
			Sentinel sentinel = new Sentinel();
			sentinel.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}