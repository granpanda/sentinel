package gp.e3.sentinel.domain.business;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.domain.repositories.RequestRepository;
import gp.e3.sentinel.util.RequestFactoryForTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RequestBusinessTest {
	
	private Connection dbConnectionMock;
	private BasicDataSource dataSourceMock;
	private RequestRepository requestRepositoryMock;
	private RequestBusiness requestBusiness;
	
	@Before
	public void setUp() {
		
		dbConnectionMock = Mockito.mock(Connection.class);
		dataSourceMock = Mockito.mock(BasicDataSource.class);
		try {
			Mockito.when(dataSourceMock.getConnection()).thenReturn(dbConnectionMock);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		requestRepositoryMock = Mockito.mock(RequestRepository.class);
		requestBusiness = new RequestBusiness(dataSourceMock, requestRepositoryMock);
	}
	
	@After
	public void tearDown() {
		
		dataSourceMock = null;
		requestRepositoryMock = null;
		requestBusiness = null;
	}
	
	@Test
	public void testCreateRequest_OK() {
		
		Request request = RequestFactoryForTests.getDefaultRequest();
		
		long expectedRequestId = 1;
		Mockito.when(requestRepositoryMock.createRequest(dbConnectionMock, request)).thenReturn(expectedRequestId);
		
		long requestId = requestBusiness.createRequest(request);
		assertEquals(expectedRequestId, requestId);
	}
	
	@Test
	public void testCreateRequest_NOK_1() {
		
		Request request = null;
		
		long expectedRequestId = 0;
		Mockito.when(requestRepositoryMock.createRequest(dbConnectionMock, request)).thenReturn(expectedRequestId);
		
		long requestId = requestBusiness.createRequest(request);
		assertEquals(expectedRequestId, requestId);
	}
	
	@Test
	public void testCreateRequest_NOK_2() {
		
		Request request = RequestFactoryForTests.getDefaultRequest();
		
		long expectedRequestId = 1;
		dbConnectionMock = null;
		Mockito.when(requestRepositoryMock.createRequest(dbConnectionMock, request)).thenReturn(expectedRequestId);
		expectedRequestId = 0; // Because the given dbConnection was null.
		
		long requestId = requestBusiness.createRequest(request);
		assertEquals(expectedRequestId, requestId);
	}
}