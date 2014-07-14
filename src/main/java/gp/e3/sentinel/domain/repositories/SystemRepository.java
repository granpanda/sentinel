package gp.e3.sentinel.domain.repositories;

import java.sql.Connection;
import java.util.List;

import redis.clients.jedis.Jedis;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.persistence.daos.SystemCacheDAO;
import gp.e3.sentinel.persistence.daos.SystemDAO;

public class SystemRepository {
	
	private final SystemDAO systemDAO;
	private final SystemCacheDAO systemCacheDAO;

	public SystemRepository(SystemDAO systemDAO, SystemCacheDAO systemCacheDAO) {
		
		this.systemDAO = systemDAO;
		this.systemCacheDAO = systemCacheDAO;
	}

	public long createSystem(Connection dbConnection, System system) {
		
		return systemDAO.createSystem(dbConnection, system);
	}
	
	public List<System> getAllSystems(Connection dbConnection) {
		
		return systemDAO.getAllSystems(dbConnection);
	}
	
	public boolean addSystemToCacheWithTimeToLive(Jedis redisClient, System system) {
		
		return systemCacheDAO.addSystemWithTimeToLive(redisClient, system);
	}
	
	public boolean isSystemInCache(Jedis redisClient, long systemId) {
		
		return systemCacheDAO.isSystemInCache(redisClient, systemId);
	}
	
	public System getSystemFromCache(Jedis redisClient, long systemId) {
		
		return systemCacheDAO.getSystem(redisClient, systemId);
	}
	
	public boolean deleteSystemFromCache(Jedis redisClient, long systemId) {
		
		return systemCacheDAO.deleteSystem(redisClient, systemId);
	}
}