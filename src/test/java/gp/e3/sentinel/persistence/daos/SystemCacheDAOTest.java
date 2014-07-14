package gp.e3.sentinel.persistence.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.infrastructure.utils.JsonUtils;
import gp.e3.sentinel.util.SystemFactoryForTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;

public class SystemCacheDAOTest {

	private Jedis redisClientMock;

	private Gson gson;
	private SystemCacheDAO systemCacheDAO;

	@Before
	public void setUp() {

		redisClientMock = Mockito.mock(Jedis.class);

		gson = JsonUtils.getDefaultGson();
		systemCacheDAO = new SystemCacheDAO(gson);
	}

	@After
	public void tearDown() {

		redisClientMock = null;
		gson = null;
		systemCacheDAO = null;
	}

	@Test
	public void testAddSystemWithTimeToLive_OK() {

		System system = SystemFactoryForTests.getDefaultSystem();
		String systemKey = system.getId() + "";
		
		long expectedValue = 1;
		Mockito.when(redisClientMock.expire(systemKey, SystemCacheDAO.TIME_TO_LIVE_IN_SECONDS)).thenReturn(expectedValue);

		boolean systemWasAddedToCache = systemCacheDAO.addSystemWithTimeToLive(redisClientMock, system);
		assertEquals(true, systemWasAddedToCache);
	}
	
	@Test
	public void testAddSystemWithTimeToLive_NOK_1() {

		System system = SystemFactoryForTests.getDefaultSystem();
		String systemKey = system.getId() + "";
		
		long expectedValue = 0;
		Mockito.when(redisClientMock.expire(systemKey, SystemCacheDAO.TIME_TO_LIVE_IN_SECONDS)).thenReturn(expectedValue);

		boolean systemWasAddedToCache = systemCacheDAO.addSystemWithTimeToLive(redisClientMock, system);
		assertEquals(false, systemWasAddedToCache);
	}

	@Test
	public void testAddSystemWithTimeToLive_NOK_2() {

		System system = null;
		boolean systemWasAddedToCache = systemCacheDAO.addSystemWithTimeToLive(redisClientMock, system);
		assertEquals(false, systemWasAddedToCache);
	}

	@Test
	public void testIsSystemInCache_OK() {

		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		String systemKey = systemId + "";
		
		boolean expectedResult = true;
		Mockito.when(redisClientMock.exists(systemKey)).thenReturn(expectedResult);
		
		boolean isSystemInCache = systemCacheDAO.isSystemInCache(redisClientMock, systemId);
		assertEquals(expectedResult, isSystemInCache);
	}
	
	@Test
	public void testIsSystemInCache_NOK_1() {

		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		String systemKey = systemId + "";
		
		boolean expectedResult = false;
		Mockito.when(redisClientMock.exists(systemKey)).thenReturn(expectedResult);
		
		boolean isSystemInCache = systemCacheDAO.isSystemInCache(redisClientMock, systemId);
		assertEquals(expectedResult, isSystemInCache);
	}
	
	@Test
	public void testIsSystemInCache_NOK_2() {

		long systemId = 0;
		boolean isSystemInCache = systemCacheDAO.isSystemInCache(redisClientMock, systemId);
		assertEquals(false, isSystemInCache);
	}
	
	@Test
	public void testGetSystem_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		String systemKey = systemId + "";
		
		String systemAsJson = gson.toJson(system);
		Mockito.when(redisClientMock.get(systemKey)).thenReturn(systemAsJson);
		
		System systemFromCache = systemCacheDAO.getSystem(redisClientMock, systemId);
		assertNotNull(systemFromCache);
		assertEquals(0, system.compareTo(systemFromCache));
	}
	
	@Test
	public void testGetSystem_NOK_1() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		String systemKey = systemId + "";
		
		Mockito.when(redisClientMock.get(systemKey)).thenReturn(SystemCacheDAO.NIL);
		
		System systemFromCache = systemCacheDAO.getSystem(redisClientMock, systemId);
		assertNull(systemFromCache);
	}
	
	@Test
	public void testGetSystem_NOK_2() {
		
		long systemId = 0;
		System systemFromCache = systemCacheDAO.getSystem(redisClientMock, systemId);
		assertNull(systemFromCache);
	}
	
	@Test
	public void testDeleteSystem_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		
		boolean expectedResult = true;
		boolean systemWasDeleted = systemCacheDAO.deleteSystem(redisClientMock, systemId);
		
		assertEquals(expectedResult, systemWasDeleted);
	}
	
	@Test
	public void testDeleteSystem_NOK() {
		
		long systemId = 0;
		boolean expectedResult = false;
		boolean systemWasDeleted = systemCacheDAO.deleteSystem(redisClientMock, systemId);
		
		assertEquals(expectedResult, systemWasDeleted);
	}
}