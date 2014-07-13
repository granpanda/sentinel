package gp.e3.sentinel.domain.business;

import static org.junit.Assert.*;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.domain.repositories.UserRepository;
import gp.e3.sentinel.util.UserFactoryForTests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UserBusinessTest {
	
	private Connection dbConnectionMock;
	private BasicDataSource dataSourceMock;
	private UserRepository userRepositoryMock;
	private UserBusiness userBusiness;
	
	@Before
	public void setUp() {
		
		dbConnectionMock = Mockito.mock(Connection.class);
		dataSourceMock = Mockito.mock(BasicDataSource.class);
		try {
			Mockito.when(dataSourceMock.getConnection()).thenReturn(dbConnectionMock);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		userRepositoryMock = Mockito.mock(UserRepository.class);
		userBusiness = new UserBusiness(dataSourceMock, userRepositoryMock);
	}
	
	@After
	public void tearDown() {
		
		dbConnectionMock = null;
		dataSourceMock = null;
		userRepositoryMock = null;
		userBusiness = null;
	}
	
	@Test
	public void testCreateUser_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		
		boolean expectedResult = true;
		Mockito.when(userRepositoryMock.createUser(dbConnectionMock, user)).thenReturn(expectedResult);
		
		boolean userWasCreated = userBusiness.createUser(user);
		assertEquals(expectedResult, userWasCreated);
	}
	
	@Test
	public void testCreateUser_NOK_1() {
		
		User user = UserFactoryForTests.getDefaultUser();
		
		boolean expectedResult = false;
		Mockito.when(userRepositoryMock.createUser(dbConnectionMock, user)).thenReturn(expectedResult);
		
		boolean userWasCreated = userBusiness.createUser(user);
		assertEquals(expectedResult, userWasCreated);
	}
	
	@Test
	public void testCreateUser_NOK_2() {
		
		User user = UserFactoryForTests.getDefaultUser();		
		Mockito.doThrow(Exception.class).when(userRepositoryMock).createUser(dbConnectionMock, user);
		boolean expectedResult = false;
		
		boolean userWasCreated = userBusiness.createUser(user);
		assertEquals(expectedResult, userWasCreated);
	}
	
	@Test
	public void testGetUserByMail_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		String mail = user.getMail();
		
		Mockito.when(userRepositoryMock.getUserByMail(dbConnectionMock, mail)).thenReturn(user);
		User retrievedUser = userBusiness.getUserByMail(mail);
		
		assertNotNull(retrievedUser);
		assertEquals(user.getMail(), retrievedUser.getMail());
		assertEquals(user.getFullName(), retrievedUser.getFullName());
		assertEquals(user.getSystemsUserIsSubscribedTo().size(), retrievedUser.getSystemsUserIsSubscribedTo().size());
	}
	
	@Test
	public void testGetUserByMail_NOK_1() {
		
		User user = null;
		String mail = "unknownMail";
		
		Mockito.when(userRepositoryMock.getUserByMail(dbConnectionMock, mail)).thenReturn(user);
		User retrievedUser = userBusiness.getUserByMail(mail);
		
		assertNull(retrievedUser);
	}
	
	@Test
	public void testGetUserByMail_NOK_2() {
		
		User user = UserFactoryForTests.getDefaultUser();
		String mail = user.getMail();
		
		Mockito.doThrow(Exception.class).when(userRepositoryMock).getUserByMail(dbConnectionMock, mail);
		User retrievedUser = userBusiness.getUserByMail(mail);
		
		assertNull(retrievedUser);
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_OK() {
		
		long systemId = 1;
		
		int listSize = 5;
		List<User> users = UserFactoryForTests.getUsersList(listSize);
		Mockito.when(userRepositoryMock.getAllUsersSubscribedToASystem(dbConnectionMock, systemId)).thenReturn(users);
		
		List<User> retrievedUsersList = userBusiness.getAllUsersSubscribedToASystem(systemId);
		assertNotNull(retrievedUsersList);
		assertEquals(listSize, retrievedUsersList.size());
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_NOK_1() {
		
		long systemId = 1;
		
		int listSize = 0;
		List<User> users = UserFactoryForTests.getUsersList(listSize);
		Mockito.when(userRepositoryMock.getAllUsersSubscribedToASystem(dbConnectionMock, systemId)).thenReturn(users);
		
		List<User> retrievedUsersList = userBusiness.getAllUsersSubscribedToASystem(systemId);
		assertNotNull(retrievedUsersList);
		assertEquals(listSize, retrievedUsersList.size());
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_NOK_2() {
		
		long systemId = 1;
		
		Mockito.doThrow(Exception.class).when(userRepositoryMock).getAllUsersSubscribedToASystem(dbConnectionMock, systemId);
		
		int listSize = 0;
		List<User> retrievedUsersList = userBusiness.getAllUsersSubscribedToASystem(systemId);
		assertNotNull(retrievedUsersList);
		assertEquals(listSize, retrievedUsersList.size());
	}
}