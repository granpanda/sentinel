package gp.e3.sentinel.service;

import static org.junit.Assert.*;

import java.util.List;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.util.SystemFactoryForTests;
import gp.e3.sentinel.util.UserFactoryForTests;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.client.ClientResponse;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

public class SystemResourceTest {
	
	private static final SystemBusiness systemBusinessMock = Mockito.mock(SystemBusiness.class);
	private static final UserBusiness userBusinessMock = Mockito.mock(UserBusiness.class);
	private static final SystemResource systemResource = new SystemResource(systemBusinessMock, userBusinessMock);

	@ClassRule
	public static final ResourceTestRule resources = ResourceTestRule.builder()
			.addResource(systemResource).build();

	private Invocation.Builder getDefaultHttpRequest(String url) {

		return resources.client().target(url).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void testCreateSystem_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		long expectedSystemId = 1;
		Mockito.when(systemBusinessMock.createSystem(Mockito.any(System.class))).thenReturn(expectedSystemId);
		
		String url = "/systems";
		Response response = getDefaultHttpRequest(url).post(Entity.entity(system, MediaType.APPLICATION_JSON));
		assertEquals(201, response.getStatus());
		
		Boolean systemWasCreated = response.readEntity(Boolean.class);
		assertEquals(true, systemWasCreated);
	}

	@Test
	public void testCreateSystem_NOK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		
		long expectedSystemId = 0;
		Mockito.when(systemBusinessMock.createSystem(Mockito.any(System.class))).thenReturn(expectedSystemId);
		
		String url = "/systems";
		Response response = getDefaultHttpRequest(url).post(Entity.entity(system, MediaType.APPLICATION_JSON));
		assertEquals(500, response.getStatus());
		
		Boolean systemWasCreated = response.readEntity(Boolean.class);
		assertEquals(false, systemWasCreated);
	}

	@Test
	public void testGetAllUsersSubscribedToASystem_OK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		
		int listSize = 5;
		List<User> expectedUsersList = UserFactoryForTests.getUsersList(listSize);
		Mockito.when(userBusinessMock.getAllUsersSubscribedToSystem(systemId)).thenReturn(expectedUsersList);
		
		String url = "/systems/" + systemId + "/users";
		Response response = getDefaultHttpRequest(url).get();
		assertEquals(200, response.getStatus());
		
		User[] retrievedUsersArray = response.readEntity(User[].class);
		assertNotNull(retrievedUsersArray);
		assertEquals(listSize, retrievedUsersArray.length);
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_NOK() {
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		
		int listSize = 0;
		List<User> expectedUsersList = UserFactoryForTests.getUsersList(listSize);
		Mockito.when(userBusinessMock.getAllUsersSubscribedToSystem(systemId)).thenReturn(expectedUsersList);
		
		String url = "/systems/" + systemId + "/users";
		Response response = getDefaultHttpRequest(url).get();
		assertEquals(200, response.getStatus());
		
		User[] retrievedUsersArray = response.readEntity(User[].class);
		assertNotNull(retrievedUsersArray);
		assertEquals(listSize, retrievedUsersArray.length);
	}
}