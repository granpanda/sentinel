package gp.e3.sentinel.service;

import static org.junit.Assert.*;

import java.util.List;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.util.SystemFactoryForTests;
import gp.e3.sentinel.util.UserFactoryForTests;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class SystemResourceTest extends ResourceTest {
	
	private SystemBusiness systemBusinessMock;
	private UserBusiness userBusinessMock;
	private SystemResource systemResource;

	@Override
	protected void setUpResources() throws Exception {
		
		systemBusinessMock = Mockito.mock(SystemBusiness.class);
		userBusinessMock = Mockito.mock(UserBusiness.class);
		systemResource = new SystemResource(systemBusinessMock, userBusinessMock);
		
		addResource(systemResource);
	}
	
	private Builder getDefaultHttpRequest(String url) {

		return client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void testCreateSystem_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		long expectedSystemId = 1;
		Mockito.when(systemBusinessMock.createSystem(Mockito.any(System.class))).thenReturn(expectedSystemId);
		
		String url = "/systems";
		ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, system);
		
		assertEquals(201, httpResponse.getStatus());
		
		Boolean systemWasCreated = httpResponse.getEntity(Boolean.class);
		assertEquals(true, systemWasCreated);
	}
	
	@Test
	public void testCreateSystem_NOK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		long expectedSystemId = 0;
		Mockito.when(systemBusinessMock.createSystem(Mockito.any(System.class))).thenReturn(expectedSystemId);
		
		String url = "/systems";
		ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, system);
		
		assertEquals(500, httpResponse.getStatus());
		
		Boolean systemWasCreated = httpResponse.getEntity(Boolean.class);
		assertEquals(false, systemWasCreated);
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		
		int listSize = 5;
		List<User> expectedUsersList = UserFactoryForTests.getUsersList(listSize);
		Mockito.when(userBusinessMock.getAllUsersSubscribedToASystem(systemId)).thenReturn(expectedUsersList);
		
		String url = "/systems/" + systemId + "/users";
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
		
		List<User> retrievedUsersList = httpResponse.getEntity(List.class);
		assertNotNull(retrievedUsersList);
		assertEquals(listSize, retrievedUsersList.size());
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_NOK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		
		int listSize = 0;
		List<User> expectedUsersList = UserFactoryForTests.getUsersList(listSize);
		Mockito.when(userBusinessMock.getAllUsersSubscribedToASystem(systemId)).thenReturn(expectedUsersList);
		
		String url = "/systems/" + systemId + "/users";
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
		
		List<User> retrievedUsersList = httpResponse.getEntity(List.class);
		assertNotNull(retrievedUsersList);
		assertEquals(listSize, retrievedUsersList.size());
	}
}