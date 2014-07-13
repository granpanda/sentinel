package gp.e3.sentinel.service;

import static org.junit.Assert.*;
import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.util.UserFactoryForTests;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;
import com.yammer.dropwizard.testing.ResourceTest;

public class UserResourceTest extends ResourceTest {
	
	private UserBusiness userBusinessMock;
	private UserResource userResource;
	
	@Override
	protected void setUpResources() throws Exception {
		
		userBusinessMock =  Mockito.mock(UserBusiness.class);
		userResource = new UserResource(userBusinessMock);
		
		addResource(userResource);
	}
	
	private Builder getDefaultHttpRequest(String url) {

		return client().resource(url)
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void testCreateUser_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		boolean expectedResult = true;
		Mockito.when(userBusinessMock.createUser(Mockito.any(User.class))).thenReturn(expectedResult);
		
		String url = "/users";
		ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, user);
		
		assertEquals(201, httpResponse.getStatus());
		Boolean responseEntity = httpResponse.getEntity(Boolean.class);
		assertEquals(expectedResult, responseEntity);
	}
	
	@Test
	public void testCreateUser_NOK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		boolean expectedResult = false;
		Mockito.when(userBusinessMock.createUser(Mockito.any(User.class))).thenReturn(expectedResult);
		
		String url = "/users";
		ClientResponse httpResponse = getDefaultHttpRequest(url).post(ClientResponse.class, user);
		
		assertEquals(500, httpResponse.getStatus());
		Boolean responseEntity = httpResponse.getEntity(Boolean.class);
		assertEquals(expectedResult, responseEntity);
	}
	
	@Test
	public void testGetUserByMail_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		String mail = user.getMail();
		
		Mockito.when(userBusinessMock.getUserByMail(mail)).thenReturn(user);
		
		String url = "/users/" + mail;
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(200, httpResponse.getStatus());
		
		User retrievedUser = httpResponse.getEntity(User.class);
		
		assertNotNull(retrievedUser);
		assertEquals(user.getMail(), retrievedUser.getMail());
		assertEquals(user.getFullName(), retrievedUser.getFullName());
		assertEquals(user.getSystemsUserIsSubscribedTo().size(), retrievedUser.getSystemsUserIsSubscribedTo().size());
	}
	
	@Test
	public void testGetUserByMail_NOK_1() {
		
		User user = UserFactoryForTests.getDefaultUser();
		String mail = user.getMail();
		
		Mockito.when(userBusinessMock.getUserByMail(mail)).thenReturn(null);
		
		String url = "/users/" + mail;
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(500, httpResponse.getStatus());
		
		String errorMessage = httpResponse.getEntity(String.class);
		
		assertNotNull(errorMessage);
		assertEquals(false, StringUtils.isBlank(errorMessage));
	}
	
	@Test
	public void testGetUserByMail_NOK_2() {
		
		String mail = "";
		
		String url = "/users/" + mail;
		ClientResponse httpResponse = getDefaultHttpRequest(url).get(ClientResponse.class);
		
		assertEquals(405, httpResponse.getStatus());
	}
}