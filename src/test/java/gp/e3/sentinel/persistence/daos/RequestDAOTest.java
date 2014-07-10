package gp.e3.sentinel.persistence.daos;

import static org.junit.Assert.*;
import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.util.RequestFactoryForTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RequestDAOTest {

	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";

	private static Connection dbConnection;
	private RequestDAO requestDAO;

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

		requestDAO = new RequestDAO();
		requestDAO.createRequestsTableIfDoesNotExist(dbConnection);
	}

	private void dropRequestsTable() {

		String dropRequestsTableSQL = "DROP TABLE requests;";

		try {
			PreparedStatement prepareStatement = dbConnection.prepareStatement(dropRequestsTableSQL);
			prepareStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {

		dropRequestsTable();
		requestDAO = null;
	}

	@Test
	public void testCreateRequest_OK() {

		Request request = RequestFactoryForTests.getDefaultRequest();

		long requestId = requestDAO.createRequest(dbConnection, request);
		assertNotEquals(0, requestId);

		Request requestById = requestDAO.getRequestById(dbConnection, requestId);
		assertNotNull(requestById);
		assertEquals(requestId, requestById.getId());

		assertEquals(request.getSystemId(), requestById.getSystemId());
		assertEquals(request.getSystemName(), requestById.getSystemName());
		assertEquals(request.getSystemUrl(), requestById.getSystemUrl());

		assertEquals(request.getHttpResponseStatusCode(), requestById.getHttpResponseStatusCode());
		assertEquals(request.getHttpResponseEntity(), requestById.getHttpResponseEntity());
		assertEquals(request.getRequestExecutionDate().toDateTime(DateTimeZone.UTC), requestById.getRequestExecutionDate());
		assertEquals(request.getRequestExecutionTimeInMilliseconds(), requestById.getRequestExecutionTimeInMilliseconds());
	}

	@Test
	public void testCreateRequest_NOK() {

		Request request = null;

		long requestId = requestDAO.createRequest(dbConnection, request);
		assertEquals(0, requestId);
	}

	@Test
	public void testGetRequestById_OK() {

		Request request = RequestFactoryForTests.getDefaultRequest();

		long requestId = requestDAO.createRequest(dbConnection, request);
		assertNotEquals(0, requestId);

		Request requestById = requestDAO.getRequestById(dbConnection, requestId);
		assertNotNull(requestById);
		assertEquals(requestId, requestById.getId());

		assertEquals(request.getSystemId(), requestById.getSystemId());
		assertEquals(request.getSystemName(), requestById.getSystemName());
		assertEquals(request.getSystemUrl(), requestById.getSystemUrl());

		assertEquals(request.getHttpResponseStatusCode(), requestById.getHttpResponseStatusCode());
		assertEquals(request.getHttpResponseEntity(), requestById.getHttpResponseEntity());
		assertEquals(request.getRequestExecutionDate().toDateTime(DateTimeZone.UTC), requestById.getRequestExecutionDate());
		assertEquals(request.getRequestExecutionTimeInMilliseconds(), requestById.getRequestExecutionTimeInMilliseconds());
	}
	
	@Test
	public void testGetRequestById_NOK() {
		
		long unknownRequestId = 3;
		Request requestById = requestDAO.getRequestById(dbConnection, unknownRequestId);
		assertNull(requestById);
	}
}