package gp.e3.sentinel.domain.repositories;

import static org.junit.Assert.*;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.persistence.daos.SystemCacheDAO;
import gp.e3.sentinel.persistence.daos.SystemDAO;
import gp.e3.sentinel.util.SystemFactoryForTests;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import redis.clients.jedis.Jedis;

public class SystemRepositoryTest {

	private Connection dbConnectionMock;
	private Jedis redisClientMock;
	
	private SystemDAO systemDAOMock;
	private SystemCacheDAO systemCacheDAOMock;
	
	private SystemRepository systemRepository;
	
	@Before
	public void setUp() {
		
		dbConnectionMock = Mockito.mock(Connection.class);
		redisClientMock = Mockito.mock(Jedis.class);
		
		systemDAOMock = Mockito.mock(SystemDAO.class);
		systemCacheDAOMock = Mockito.mock(SystemCacheDAO.class);
		
		systemRepository = new SystemRepository(systemDAOMock, systemCacheDAOMock);
	}
	
	@After
	public void tearDown() {
		
		dbConnectionMock = null;
		systemDAOMock = null;
		systemRepository = null;
	}
	
	@Test
	public void testCreateSystem_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		long expectedSystemId = 1;
		Mockito.when(systemDAOMock.createSystem(dbConnectionMock, system)).thenReturn(expectedSystemId);
		long systemId = systemRepository.createSystem(dbConnectionMock, system);
		
		assertEquals(expectedSystemId, systemId);
	}
	
	@Test
	public void testCreateSystem_NOK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		long expectedSystemId = 0;
		Mockito.when(systemDAOMock.createSystem(dbConnectionMock, system)).thenReturn(expectedSystemId);
		long systemId = systemRepository.createSystem(dbConnectionMock, system);
		
		assertEquals(expectedSystemId, systemId);
	}
	
	@Test
	public void testGetAllSystems_OK() {
		
		int listSize = 5;
		List<System> systems = SystemFactoryForTests.getSystemsList(listSize);
		
		Mockito.when(systemDAOMock.getAllSystems(dbConnectionMock)).thenReturn(systems);
		List<System> retrievedSystemsList = systemRepository.getAllSystems(dbConnectionMock);
		
		assertNotNull(retrievedSystemsList);
		assertEquals(listSize, retrievedSystemsList.size());
	}
	
	@Test
	public void testGetAllSystems_NOK() {
		
		int listSize = 0;
		List<System> systems = SystemFactoryForTests.getSystemsList(listSize);
		
		Mockito.when(systemDAOMock.getAllSystems(dbConnectionMock)).thenReturn(systems);
		List<System> retrievedSystemsList = systemRepository.getAllSystems(dbConnectionMock);
		
		assertNotNull(retrievedSystemsList);
		assertEquals(listSize, retrievedSystemsList.size());
	}
	
	@Test
	public void testAddSystemToCacheWithTimeToLive_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		boolean expectedResult = true;
		Mockito.when(systemCacheDAOMock.addSystemWithTimeToLive(redisClientMock, system)).thenReturn(expectedResult);
		
		boolean systemWasAddedToCache = systemRepository.addSystemToCacheWithTimeToLive(redisClientMock, system);
		assertEquals(expectedResult, systemWasAddedToCache);
	}
	
	@Test
	public void testAddSystemToCacheWithTimeToLive_NOK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		boolean expectedResult = false;
		Mockito.when(systemCacheDAOMock.addSystemWithTimeToLive(redisClientMock, system)).thenReturn(expectedResult);
		
		boolean systemWasAddedToCache = systemRepository.addSystemToCacheWithTimeToLive(redisClientMock, system);
		assertEquals(expectedResult, systemWasAddedToCache);
	}
	
	@Test
	public void testIsSystemInCache_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		
		boolean expectedResult = true;
		Mockito.when(systemCacheDAOMock.isSystemInCache(redisClientMock, systemId)).thenReturn(expectedResult);
		
		boolean isSystemInCache = systemRepository.isSystemInCache(redisClientMock, systemId);
		assertEquals(expectedResult, isSystemInCache);
	}
	
	@Test
	public void testIsSystemInCache_NOK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		
		boolean expectedResult = false;
		Mockito.when(systemCacheDAOMock.isSystemInCache(redisClientMock, systemId)).thenReturn(expectedResult);
		
		boolean isSystemInCache = systemRepository.isSystemInCache(redisClientMock, systemId);
		assertEquals(expectedResult, isSystemInCache);
	}
	
	@Test
	public void testGetSystemFromCache_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		Mockito.when(systemCacheDAOMock.getSystem(redisClientMock, systemId)).thenReturn(system);
		
		System systemFromCache = systemRepository.getSystemFromCache(redisClientMock, systemId);
		assertNotNull(systemFromCache);
		assertEquals(0, system.compareTo(systemFromCache));
	}
	
	@Test
	public void testGetSystemFromCache_NOK() {
		
		System system = null;
		long systemId = 1;
		Mockito.when(systemCacheDAOMock.getSystem(redisClientMock, systemId)).thenReturn(system);
		
		System systemFromCache = systemRepository.getSystemFromCache(redisClientMock, systemId);
		assertNull(systemFromCache);
	}
	
	@Test
	public void testDeleteSystemFromCache_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		
		boolean expectedResult = true;
		Mockito.when(systemCacheDAOMock.deleteSystem(redisClientMock, systemId)).thenReturn(expectedResult);
		
		boolean systemWasDeletedFromCache = systemRepository.deleteSystemFromCache(redisClientMock, systemId);
		assertEquals(expectedResult, systemWasDeletedFromCache);
	}
	
	@Test
	public void testDeleteSystemFromCache_NOK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		
		boolean expectedResult = false;
		Mockito.when(systemCacheDAOMock.deleteSystem(redisClientMock, systemId)).thenReturn(expectedResult);
		
		boolean systemWasDeletedFromCache = systemRepository.deleteSystemFromCache(redisClientMock, systemId);
		assertEquals(expectedResult, systemWasDeletedFromCache);
	}
}