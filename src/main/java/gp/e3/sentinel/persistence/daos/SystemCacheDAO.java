package gp.e3.sentinel.persistence.daos;

import gp.e3.sentinel.domain.entities.System;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;

public class SystemCacheDAO {
	
	public static final String NIL = "nil";
	public static final int TIME_TO_LIVE_IN_SECONDS = 60 * 15; // 15 minutes.
	
	private Gson gson;

	public SystemCacheDAO(Gson gson) {
		
		this.gson = gson;
	}

	public boolean addSystemWithTimeToLive(Jedis redisClient, System system) {
		
		boolean systemWasAddedToCache = false;
		
		if (System.isValidSystem(system)) {
			
			String systemAsJson = gson.toJson(system);
			
			String systemKey = system.getId() + "";
			redisClient.set(systemKey, systemAsJson);
			Long expireOperationResult = redisClient.expire(systemKey, TIME_TO_LIVE_IN_SECONDS);
			
			systemWasAddedToCache = (expireOperationResult == 1);
		}
		
		return systemWasAddedToCache;
	}
	
	public boolean isSystemInCache(Jedis redisClient, long systemId) {
		
		boolean systemExistsInCache = false;
		
		if (systemId > 0) {

			systemExistsInCache = redisClient.exists(systemId + "");
		}
		
		return systemExistsInCache;
	}
	
	public System getSystem(Jedis redisClient, long systemId) {
		
		System system = null;
		
		if (systemId > 0) {
			
			String systemAsJson = redisClient.get(systemId + "");
			
			if (!StringUtils.isBlank(systemAsJson) && !systemAsJson.equalsIgnoreCase(NIL)) {
				
				try {
					system = gson.fromJson(systemAsJson, System.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return system;
	}
	
	public boolean deleteSystem(Jedis redisClient, long systemId) {
		
		boolean systemWasDeletedFromCache = false;
		
		if (systemId > 0) {
			
			redisClient.del(systemId + "");
			systemWasDeletedFromCache = true;
		}
		
		return systemWasDeletedFromCache;
	}
}