package gp.e3.sentinel.domain.business;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class SystemBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemBusiness.class);

	private final JedisPool redisPool;
	private final BasicDataSource dataSource;
	private final SystemRepository systemRepository;

    public SystemBusiness(JedisPool redisPool, BasicDataSource dataSource, SystemRepository systemRepository) {
        this.redisPool = redisPool;
        this.dataSource = dataSource;
        this.systemRepository = systemRepository;
    }

    public long createSystem(System system) {

		long systemId = 0;
		Connection dbConnection = null;

		try {
			dbConnection = dataSource.getConnection();
			systemId = systemRepository.createSystem(dbConnection, system);
		} catch (Exception e) {
            LOGGER.error("createSystem", e);
		} finally {
			SqlUtils.closeDbConnection(dbConnection);
		}

		return systemId;
	}

	public List<System> getAllSystems() {

		List<System> systems = new ArrayList<System>();
		Connection dbConnection = null;

		try {
			dbConnection = dataSource.getConnection();
			systems = systemRepository.getAllSystems(dbConnection);
		} catch (Exception e) {
			LOGGER.error("getAllSystems", e);
		} finally {
			SqlUtils.closeDbConnection(dbConnection);
		}

		return systems;
	}

	public boolean isSystemInCache(long systemId) {

		Jedis redisClient = redisPool.getResource();
		boolean isSystemInCache = systemRepository.isSystemInCache(redisClient, systemId);
		redisClient.close();

		return isSystemInCache;
	}

    public boolean addSystemToCache(System system) {

        Jedis redisClient = redisPool.getResource();
        boolean systemWasAddedToCache = systemRepository.addSystemToCacheWithTimeToLive(redisClient, system);
        redisClient.close();

        return systemWasAddedToCache;
    }

    public boolean deleteSystemFromCache(long systemId) {

        Jedis redisClient = redisPool.getResource();
        boolean systemWasDeletedFromCache = systemRepository.deleteSystemFromCache(redisClient, systemId);
        redisClient.close();

        return systemWasDeletedFromCache;
    }
}