package gp.e3.sentinel.service;

import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.util.UserFactoryForTests;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.commons.lang3.StringUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserResourceTest {
	
	private static final UserBusiness userBusinessMock = Mockito.mock(UserBusiness.class);
	private static final UserResource userResource = new UserResource(userBusinessMock);

	@ClassRule
	public static final ResourceTestRule resources = ResourceTestRule.builder()
			.addResource(userResource).build();

	private Invocation.Builder getDefaultHttpRequest(String url) {

		return resources.client().target(url).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void testCreateUser_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		boolean expectedResult = true;
		Mockito.when(userBusinessMock.createUser(Mockito.any(User.class))).thenReturn(expectedResult);
		
		String url = "/users";
		Response response = getDefaultHttpRequest(url).post(Entity.entity(user, MediaType.APPLICATION_JSON));
		assertEquals(201, response.getStatus());

		Boolean responseEntity = response.readEntity(Boolean.class);
		assertEquals(expectedResult, responseEntity);
	}
	
	@Test
	public void testCreateUser_NOK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		boolean expectedResult = false;
		Mockito.when(userBusinessMock.createUser(Mockito.any(User.class))).thenReturn(expectedResult);
		
		String url = "/users";
		Response response = getDefaultHttpRequest(url).post(Entity.entity(user, MediaType.APPLICATION_JSON));
		assertEquals(500, response.getStatus());

		Boolean responseEntity = response.readEntity(Boolean.class);
		assertEquals(expectedResult, responseEntity);
	}
	
	@Test
	public void testGetUserByMail_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		String mail = user.getMail();
		
		Mockito.when(userBusinessMock.getUserByMail(mail)).thenReturn(user);
		
		String url = "/users/" + mail;
		Response response = getDefaultHttpRequest(url).get();
		assertEquals(200, response.getStatus());
		
		User retrievedUser = response.readEntity(User.class);
		
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
		Response response = getDefaultHttpRequest(url).get();
		assertEquals(500, response.getStatus());
		
		String errorMessage = response.readEntity(String.class);
		assertNotNull(errorMessage);
		assertEquals(false, StringUtils.isBlank(errorMessage));
	}
	
	@Test
	public void testGetUserByMail_NOK_2() {
		
		String mail = "";
		String url = "/users/" + mail;
		Response response = getDefaultHttpRequest(url).get();
		assertEquals(405, response.getStatus());
	}
}