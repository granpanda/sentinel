package gp.e3.sentinel.domain.repositories;

import static org.junit.Assert.*;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.persistence.daos.SystemDAO;
import gp.e3.sentinel.util.SystemFactoryForTests;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SystemRepositoryTest {

	private Connection dbConnectionMock;
	private SystemDAO systemDAOMock;
	private SystemRepository systemRepository;
	
	@Before
	public void setUp() {
		
		dbConnectionMock = Mockito.mock(Connection.class);
		systemDAOMock = Mockito.mock(SystemDAO.class);
		systemRepository = new SystemRepository(systemDAOMock);
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
}