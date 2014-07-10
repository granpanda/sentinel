package gp.e3.sentinel.persistence.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.util.SystemFactoryForTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SystemDAOTest {
	
	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static Connection dbConnection;
	private SystemDAO systemDAO;
	
	@BeforeClass
	public static void setUpClass() {

		try {

			Class.forName("org.h2.Driver");
			dbConnection = DriverManager.getConnection(H2_IN_MEMORY_DB);

		} catch (ClassNotFoundException | SQLException e) {

			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownClass() {

		try {

			dbConnection.close();

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			dbConnection = null;
		}
	}
	
	@Before
	public void setUp() {

		systemDAO = new SystemDAO();
		systemDAO.createSystemsTableIfDoesNotExist(dbConnection);
	}

	private void dropSystemsTable() {

		String dropSystemsTableSQL = "DROP TABLE systems;";

		try {
			PreparedStatement prepareStatement = dbConnection.prepareStatement(dropSystemsTableSQL);
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {

		dropSystemsTable();
		systemDAO = null;
	}
	
	@Test
	public void testCreateSystem_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = systemDAO.createSystem(dbConnection, system);
		assertNotEquals(0, systemId);
		
		List<System> allSystems = systemDAO.getAllSystems(dbConnection);
		assertEquals(1, allSystems.size());
		
		System retrievedSystem = allSystems.get(0);
		assertEquals(systemId, retrievedSystem.getId());
		assertEquals(system.getName(), retrievedSystem.getName());
		assertEquals(system.getUrl(), retrievedSystem.getUrl());
	}
	
	@Test
	public void testCreateSystem_NOK() {
		
		System system = null;
		long systemId = systemDAO.createSystem(dbConnection, system);
		assertEquals(0, systemId);
	}
	
	@Test
	public void testGetAllSystems_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = systemDAO.createSystem(dbConnection, system);
		assertNotEquals(0, systemId);
		
		List<System> allSystems = systemDAO.getAllSystems(dbConnection);
		assertNotNull(allSystems);
		assertEquals(1, allSystems.size());
		
		System retrievedSystem = allSystems.get(0);
		assertEquals(systemId, retrievedSystem.getId());
		assertEquals(system.getName(), retrievedSystem.getName());
		assertEquals(system.getUrl(), retrievedSystem.getUrl());
	}
	
	@Test
	public void testGetAllSystems_NOK() {
		
		List<System> allSystems = systemDAO.getAllSystems(dbConnection);
		assertNotNull(allSystems);
		assertEquals(0, allSystems.size());
	}
}