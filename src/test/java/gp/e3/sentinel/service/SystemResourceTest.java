package gp.e3.sentinel.service;

import static org.junit.Assert.*;
import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.util.SystemFactoryForTests;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class SystemResourceTest extends ResourceTest {
	
	private SystemBusiness systemBusinessMock;
	private SystemResource systemResource;

	@Override
	protected void setUpResources() throws Exception {
		
		systemBusinessMock = Mockito.mock(SystemBusiness.class);
		systemResource = new SystemResource(systemBusinessMock);
		
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
}