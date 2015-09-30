package gp.e3.sentinel.domain.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.util.SystemFactoryForTests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SystemBusinessTest {

	private Jedis redisClient;
	private JedisPool redisPool;
	
	private Connection dbConnectionMock;
	private BasicDataSource dataSourceMock;
	
	private SystemRepository systemRepositoryMock;
	private SystemBusiness systemBusiness;
	
	@Before
	public void setUp() {

		redisClient = Mockito.mock(Jedis.class);
		redisPool = Mockito.mock(JedisPool.class);
		Mockito.when(redisPool.getResource()).thenReturn(redisClient);
		
		dbConnectionMock = Mockito.mock(Connection.class);
		dataSourceMock = Mockito.mock(BasicDataSource.class);

		try {
			Mockito.when(dataSourceMock.getConnection()).thenReturn(dbConnectionMock);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		systemRepositoryMock = Mockito.mock(SystemRepository.class);
		systemBusiness = new SystemBusiness(redisPool, dataSourceMock, systemRepositoryMock);
	}
	
	@After
	public void tearDown() {

		redisClient = null;
		redisPool = null;
		
		dbConnectionMock = null;
		dataSourceMock = null;

		systemRepositoryMock = null;
		systemBusiness = null;
	}
	
	@Test
	public void testCreateSystem_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		long expectedSystemId = 1;
		Mockito.when(systemRepositoryMock.createSystem(dbConnectionMock, system)).thenReturn(expectedSystemId);
		
		long systemId = systemBusiness.createSystem(system);
		assertEquals(expectedSystemId, systemId);
	}
	
	@Test
	public void testCreateSystem_NOK_1() {
		
		System system = null;
		
		long expectedSystemId = 0;
		Mockito.when(systemRepositoryMock.createSystem(dbConnectionMock, system)).thenReturn(expectedSystemId);
		
		long systemId = systemBusiness.createSystem(system);
		assertEquals(expectedSystemId, systemId);
	}
	
	@Test
	public void testCreateSystem_NOK_2() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		long expectedSystemId = 1;
		dbConnectionMock = null;
		Mockito.when(systemRepositoryMock.createSystem(dbConnectionMock, system)).thenReturn(expectedSystemId);
		expectedSystemId = 0; // Because the given dbConnection was null.
		
		long systemId = systemBusiness.createSystem(system);
		assertEquals(expectedSystemId, systemId);
	}
	
	@Test
	public void testGetAllSystems_OK() {
		
		int listSize = 5;
		List<System> systems = SystemFactoryForTests.getSystemsList(listSize);
		Mockito.when(systemRepositoryMock.getAllSystems(dbConnectionMock)).thenReturn(systems);
		
		List<System> retrievedSystemsList = systemBusiness.getAllSystems();
		assertNotNull(retrievedSystemsList);
		assertEquals(listSize, retrievedSystemsList.size());
	}
	
	@Test
	public void testGetAllSystems_NOK() {
		
		int listSize = 0;
		List<System> systems = SystemFactoryForTests.getSystemsList(listSize);
		Mockito.when(systemRepositoryMock.getAllSystems(dbConnectionMock)).thenReturn(systems);
		
		List<System> retrievedSystemsList = systemBusiness.getAllSystems();
		assertNotNull(retrievedSystemsList);
		assertEquals(listSize, retrievedSystemsList.size());
	}

	@Test
	public void testIsSystemInCache_OK() {

		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();

		boolean expectedValue = true;
		Mockito.when(systemRepositoryMock.isSystemInCache(redisClient, systemId)).thenReturn(expectedValue);

		boolean isSystemInCache = systemBusiness.isSystemInCache(systemId);
		assertEquals(expectedValue, isSystemInCache);;

	}

	@Test
	public void testIsSystemInCache_NOK() {

		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();

		boolean expectedValue = false;
		Mockito.when(systemRepositoryMock.isSystemInCache(redisClient, systemId)).thenReturn(expectedValue);

		boolean isSystemInCache = systemBusiness.isSystemInCache(systemId);
		assertEquals(expectedValue, isSystemInCache);;

	}

	@Test
	public void testAddSystemToCache_OK() {

		System system = SystemFactoryForTests.getDefaultSystem();

		boolean expectedValue = true;
		Mockito.when(systemRepositoryMock.addSystemToCacheWithTimeToLive(redisClient, system)).thenReturn(expectedValue);

		boolean systemWasAddedToCache = systemBusiness.addSystemToCache(system);
		assertEquals(expectedValue, systemWasAddedToCache);
	}

	@Test
	public void testAddSystemToCache_NOK() {

		System system = SystemFactoryForTests.getDefaultSystem();

		boolean expectedValue = false;
		Mockito.when(systemRepositoryMock.addSystemToCacheWithTimeToLive(redisClient, system)).thenReturn(expectedValue);

		boolean systemWasAddedToCache = systemBusiness.addSystemToCache(system);
		assertEquals(expectedValue, systemWasAddedToCache);
	}

	@Test
	public void testDeleteSystemFromCache_OK() {

		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();

		boolean expectedValue = true;
		Mockito.when(systemRepositoryMock.deleteSystemFromCache(redisClient, systemId)).thenReturn(expectedValue);

		boolean systemWasDeletedFromCache = systemBusiness.deleteSystemFromCache(systemId);
		assertEquals(expectedValue, systemWasDeletedFromCache);
	}
	
	@Test
	public void testDeleteSystemFromCache_NOK() {

		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();

		boolean expectedValue = false;
		Mockito.when(systemRepositoryMock.deleteSystemFromCache(redisClient, systemId)).thenReturn(expectedValue);

		boolean systemWasDeletedFromCache = systemBusiness.deleteSystemFromCache(systemId);
		assertEquals(expectedValue, systemWasDeletedFromCache);
	}
}