package gp.e3.sentinel.domain.repositories;

import static org.junit.Assert.*;
import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.persistence.daos.RequestDAO;
import gp.e3.sentinel.util.RequestFactoryForTests;

import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RequestRepositoryTest {
	
	private Connection dbConnectionMock;
	private RequestDAO requestDAOMock;
	private RequestRepository requestRepository;
	
	@Before
	public void setUp() {
		
		dbConnectionMock = Mockito.mock(Connection.class);
		requestDAOMock = Mockito.mock(RequestDAO.class);
		requestRepository = new RequestRepository(requestDAOMock);
	}
	
	@After
	public void tearDown() {
		
		dbConnectionMock = null;
		requestDAOMock = null;
		requestRepository = null;
	}
	
	@Test
	public void testCreateRequest_OK() {
		
		Request request = RequestFactoryForTests.getDefaultRequest();
		
		long expectedRequestId = 1;
		Mockito.when(requestDAOMock.createRequest(dbConnectionMock, request)).thenReturn(expectedRequestId);
		long requestId = requestRepository.createRequest(dbConnectionMock, request);
		
		assertEquals(expectedRequestId, requestId);
	}
	
	@Test
	public void testCreateRequest_NOK() {
		
		Request request = RequestFactoryForTests.getDefaultRequest();
		
		long expectedRequestId = 0;
		Mockito.when(requestDAOMock.createRequest(dbConnectionMock, request)).thenReturn(expectedRequestId);
		long requestId = requestRepository.createRequest(dbConnectionMock, request);
		
		assertEquals(expectedRequestId, requestId);
	}
}