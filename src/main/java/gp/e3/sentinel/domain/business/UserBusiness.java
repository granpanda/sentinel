package gp.e3.sentinel.domain.business;

import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.domain.repositories.UserRepository;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserBusiness {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserBusiness.class);
	
	private final BasicDataSource dataSource;
	private final UserRepository userRepository;
	
	public UserBusiness(BasicDataSource dataSource, UserRepository userRepository) {
		
		this.dataSource = dataSource;
		this.userRepository = userRepository;
	}
	
	public boolean createUser(User user) {
		
		boolean userWasCreated = false;
		Connection dbConnection = null;
		
		try {

			dbConnection = dataSource.getConnection();
			dbConnection.setAutoCommit(false);

			if (userRepository.createUser(dbConnection, user)) {
				dbConnection.commit();
				userWasCreated = true;
			}

		} catch (Exception e) {
			LOGGER.error("createUser", e);
			SqlUtils.rollbackTransaction(dbConnection);
		} finally {
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return userWasCreated;
	}
	
	public User getUserByMail(String mail) {
		
		User user = null;
		Connection dbConnection = null;
		
		try {
			dbConnection = dataSource.getConnection();
			user = userRepository.getUserByMail(dbConnection, mail);
		} catch (Exception e) {
			LOGGER.error("getUserByMail", e);
		} finally {
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return user;
	}
	
	public List<User> getAllUsersSubscribedToSystem(long systemId) {
		
		List<User> users = new ArrayList<User>();
		Connection dbConnection = null;
		
		try {
			dbConnection = dataSource.getConnection();
			users = userRepository.getAllUsersSubscribedToASystem(dbConnection, systemId);
		} catch (Exception e) {
			LOGGER.error("getAllUsersSubscribedToSystem", e);
		} finally {
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return users;
	}
}