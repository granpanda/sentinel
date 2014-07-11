package gp.e3.sentinel.domain.repositories;

import java.sql.Connection;
import java.util.List;

import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.persistence.daos.UserDAO;

public class UserRepository {
	
	private final UserDAO userDAO;

	public UserRepository(UserDAO userDAO) {
		
		this.userDAO = userDAO;
	}
	
	public boolean createUser(Connection dbConnection, User user) {
		
		int affectedRows = userDAO.createUser(dbConnection, user);
		return (affectedRows == 1);
	}
	
	public User getUserByMail(Connection dbConnection, String mail) {
		
		return userDAO.getUserByMail(dbConnection, mail);
	}
	
	public List<User> getAllUsersSubscribedToASystem(Connection dbConnection, long systemId) {
		
		return userDAO.getAllUsersSubscribedToASystem(dbConnection, systemId);
	}
}