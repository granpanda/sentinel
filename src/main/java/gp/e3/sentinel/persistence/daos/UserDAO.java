package gp.e3.sentinel.persistence.daos;

import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.persistence.mappers.UserMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
	
	public static final String MAIL = "mail";
	public static final String FULL_NAME = "fullName";
	public static final String SYSTEMS_WHICH_SUBSCRIBED_TO = "systemsWhichIsSubscribedTo";
	
	public int createUsersTableTableIfDoesNotExist(Connection dbConnection) {
		
		int result = 0;
		String createUsersTableTableIfDoesNotExistSQL = "CREATE TABLE IF NOT EXISTS users ("
				+ "mail VARCHAR(128) PRIMARY KEY, fullName VARCHAR(32) NOT NULL);";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(createUsersTableTableIfDoesNotExistSQL);
			result = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int createUser(Connection dbConnection, User user) {
		
		int affectedRows = 0;
		String createUserSQL = "INSERT INTO users (mail, fullName) VALUES (? ,?);";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(createUserSQL);
			prepareStatement.setString(1, user.getMail());
			prepareStatement.setString(2, user.getFullName());
			
			affectedRows = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return affectedRows;
	}
	
	public User getUserByMail(Connection dbConnection, String mail) {
		
		User retrievedUser = null;
		String getUserByMailSQL = "SELECT * FROM users WHERE mail = ?";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getUserByMailSQL);
			prepareStatement.setString(1, mail);
			
			ResultSet resultSet = prepareStatement.executeQuery();
			retrievedUser = UserMapper.getSingleUser(resultSet);
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return retrievedUser;
	}
}