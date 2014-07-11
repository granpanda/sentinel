package gp.e3.sentinel.domain.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.persistence.daos.UserDAO;
import gp.e3.sentinel.util.UserFactoryForTests;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UserRepositoryTest {
	
	private Connection dbConnectionMock;
	private UserDAO userDAOMock;
	private UserRepository userRepository;
	
	@Before
	public void setUp() {
		
		dbConnectionMock = Mockito.mock(Connection.class);
		userDAOMock = Mockito.mock(UserDAO.class);
		userRepository = new UserRepository(userDAOMock);
	}
	
	@After
	public void tearDown() {
		
		dbConnectionMock = null;
		userDAOMock = null;
		userRepository = null;
	}
	
	@Test
	public void testCreateUser_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		
		int affectedRows = 1;
		Mockito.when(userDAOMock.createUser(dbConnectionMock, user)).thenReturn(affectedRows);
		
		boolean userWasCreated = userRepository.createUser(dbConnectionMock, user);
		assertEquals(true, userWasCreated);
	}
	
	@Test
	public void testCreateUser_NOK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		
		int affectedRows = 0;
		Mockito.when(userDAOMock.createUser(dbConnectionMock, user)).thenReturn(affectedRows);
		
		boolean userWasCreated = userRepository.createUser(dbConnectionMock, user);
		assertEquals(false, userWasCreated);
	}
	
	@Test
	public void testGetUserByMail_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		
		String mail = user.getMail();
		Mockito.when(userDAOMock.getUserByMail(dbConnectionMock, mail)).thenReturn(user);
		
		User retrievedUser = userRepository.getUserByMail(dbConnectionMock, mail);
		
		assertNotNull(retrievedUser);
		assertEquals(mail, retrievedUser.getMail());
		assertEquals(user.getFullName(), retrievedUser.getFullName());
	}
	
	@Test
	public void testGetUserByMail_NOK_1() {
		
		User user = null;
		String mail = "";
		Mockito.when(userDAOMock.getUserByMail(dbConnectionMock, mail)).thenReturn(user);
		
		User retrievedUser = userRepository.getUserByMail(dbConnectionMock, mail);
		assertNull(retrievedUser);
	}
	
	@Test
	public void testGetUserByMail_NOK_2() {
		
		User user = null;
		String mail = null;
		Mockito.when(userDAOMock.getUserByMail(dbConnectionMock, mail)).thenReturn(user);
		
		User retrievedUser = userRepository.getUserByMail(dbConnectionMock, mail);
		assertNull(retrievedUser);
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_OK() {
		
		int listSize = 5;
		List<User> usersList = UserFactoryForTests.getUsersList(listSize);
		
		long systemId = 1;
		Mockito.when(userDAOMock.getAllUsersSubscribedToASystem(dbConnectionMock, systemId)).thenReturn(usersList);
		
		List<User> retrievedUsersList = userRepository.getAllUsersSubscribedToASystem(dbConnectionMock, systemId);
		
		assertNotNull(retrievedUsersList);
		assertEquals(listSize, retrievedUsersList.size());
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_NOK() {
		
		int listSize = 0;
		List<User> usersList = UserFactoryForTests.getUsersList(listSize);
		
		long systemId = 1;
		Mockito.when(userDAOMock.getAllUsersSubscribedToASystem(dbConnectionMock, systemId)).thenReturn(usersList);
		
		List<User> retrievedUsersList = userRepository.getAllUsersSubscribedToASystem(dbConnectionMock, systemId);
		
		assertNotNull(retrievedUsersList);
		assertEquals(listSize, retrievedUsersList.size());
	}
}