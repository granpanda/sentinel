package gp.e3.sentinel.persistence.daos;

import static org.junit.Assert.*;
import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.util.UserFactoryForTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserDAOTest {
	
	public static final String H2_IN_MEMORY_DB = "jdbc:h2:mem:test";
	
	private static Connection dbConnection;
	private UserDAO userDAO;
	
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
		
		userDAO = new UserDAO();
		userDAO.createUsersTablesIfNeeded(dbConnection);
	}
	
	@After
	public void tearDown() {
		
		String dropUsersTableSQL = "DROP TABLE users;";
		String dropSystemsAndUsersTableSQL = "DROP TABLE systems_users;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(dropUsersTableSQL);
			prepareStatement.executeUpdate();
			prepareStatement.close();
			
			PreparedStatement prepareStatement2 = dbConnection.prepareStatement(dropSystemsAndUsersTableSQL);
			prepareStatement2.executeUpdate();
			prepareStatement2.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		userDAO = null;
	}
	
	@Test
	public void testCreateUser_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		int affectedRows = userDAO.createUser(dbConnection, user);
		assertEquals(1, affectedRows);
		
		User userByMail = userDAO.getUserByMail(dbConnection, user.getMail());
		assertNotNull(userByMail);
		
		assertEquals(user.getMail(), userByMail.getMail());
		assertEquals(user.getFullName(), userByMail.getFullName());
		assertEquals(user.getSystemsUserIsSubscribedTo().size(), userByMail.getSystemsUserIsSubscribedTo().size());
	}
	
	@Test
	public void testCreateUser_NOK() {
		
		User user = null;
		int affectedRows = userDAO.createUser(dbConnection, user);
		assertEquals(0, affectedRows);
	}
	
	@Test
	public void testGetUserByMail_OK() {
		
		User user = UserFactoryForTests.getDefaultUser();
		int affectedRows = userDAO.createUser(dbConnection, user);
		assertEquals(1, affectedRows);
		
		User userByMail = userDAO.getUserByMail(dbConnection, user.getMail());
		assertNotNull(userByMail);
		
		assertEquals(user.getMail(), userByMail.getMail());
		assertEquals(user.getFullName(), userByMail.getFullName());
		assertEquals(user.getSystemsUserIsSubscribedTo().size(), userByMail.getSystemsUserIsSubscribedTo().size());
	}
	
	@Test
	public void testGetUserByMail_NOK_1() {
		
		User user = UserFactoryForTests.getDefaultUser();
		String mail = user.getMail();
		User userByMail = userDAO.getUserByMail(dbConnection, mail);
		
		assertNull(userByMail);
	}
	
	@Test
	public void testGetUserByMail_NOK_2() {
		
		User user = UserFactoryForTests.getDefaultUser();
		int affectedRows = userDAO.createUser(dbConnection, user);
		assertEquals(1, affectedRows);
		
		String mail = "";
		User userByMail = userDAO.getUserByMail(dbConnection, mail);
		
		assertNull(userByMail);
	}
	
	@Test
	public void testGetUserByMail_NOK_3() {
		
		User user = UserFactoryForTests.getDefaultUser();
		int affectedRows = userDAO.createUser(dbConnection, user);
		assertEquals(1, affectedRows);
		
		String mail = null;
		User userByMail = userDAO.getUserByMail(dbConnection, mail);
		
		assertNull(userByMail);
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_OK() {
		
		long systemId = 1;
		List<Long> systemsUserIsSubscribedTo = new ArrayList<Long>();
		systemsUserIsSubscribedTo.add(systemId);
		
		User user1 = UserFactoryForTests.getUserSubscribedToSystems(1, systemsUserIsSubscribedTo);
		User user2 = UserFactoryForTests.getUserSubscribedToSystems(2, systemsUserIsSubscribedTo);
		User user3 = UserFactoryForTests.getUserSubscribedToSystems(3, systemsUserIsSubscribedTo);
		User user4 = UserFactoryForTests.getUserSubscribedToSystems(4, systemsUserIsSubscribedTo);
		User user5 = UserFactoryForTests.getUserSubscribedToSystems(5, systemsUserIsSubscribedTo);
		
		assertEquals(1, userDAO.createUser(dbConnection, user1));
		assertEquals(1, userDAO.createUser(dbConnection, user2));
		assertEquals(1, userDAO.createUser(dbConnection, user3));
		assertEquals(1, userDAO.createUser(dbConnection, user4));
		assertEquals(1, userDAO.createUser(dbConnection, user5));
		
		List<User> retrievedUsers = userDAO.getAllUsersSubscribedToASystem(dbConnection, systemId);
		assertNotNull(retrievedUsers);
		assertEquals(5, retrievedUsers.size());
	}
	
	@Test
	public void testGetAllUsersSubscribedToASystem_NOK() {
		
		long systemId = 1;
		List<Long> systemsUserIsSubscribedTo = new ArrayList<Long>();
		systemsUserIsSubscribedTo.add(systemId);
		
		User user1 = UserFactoryForTests.getUserSubscribedToSystems(1, systemsUserIsSubscribedTo);
		User user2 = UserFactoryForTests.getUserSubscribedToSystems(2, systemsUserIsSubscribedTo);
		User user3 = UserFactoryForTests.getUserSubscribedToSystems(3, systemsUserIsSubscribedTo);
		User user4 = UserFactoryForTests.getUserSubscribedToSystems(4, systemsUserIsSubscribedTo);
		User user5 = UserFactoryForTests.getUserSubscribedToSystems(5, systemsUserIsSubscribedTo);
		
		assertEquals(1, userDAO.createUser(dbConnection, user1));
		assertEquals(1, userDAO.createUser(dbConnection, user2));
		assertEquals(1, userDAO.createUser(dbConnection, user3));
		assertEquals(1, userDAO.createUser(dbConnection, user4));
		assertEquals(1, userDAO.createUser(dbConnection, user5));
		
		long notExistentSystemId = 3;
		List<User> retrievedUsers = userDAO.getAllUsersSubscribedToASystem(dbConnection, notExistentSystemId);
		assertNotNull(retrievedUsers);
		assertEquals(0, retrievedUsers.size());
	}
}