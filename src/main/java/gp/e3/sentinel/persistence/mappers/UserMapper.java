package gp.e3.sentinel.persistence.mappers;

import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.persistence.daos.UserDAO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {

	private static User getUserFromResultSet(ResultSet resultSet, List<Long> systemsUserIsSubscribedTo) {

		User user = null;

		try {

			String mail = resultSet.getString(UserDAO.MAIL);
			String fullName = resultSet.getString(UserDAO.FULL_NAME);
			user = new User(mail, fullName, systemsUserIsSubscribedTo);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return user;
	}
	
	public static List<Long> getSystemsUserIsSubscribedToFromResultSet(ResultSet resultSet) {
		
		List<Long> systemsUserIsSubscribedTo = new ArrayList<Long>();
		
		try {
			
			while (resultSet.next()) {
				
				long systemId = resultSet.getLong(UserDAO.SYSTEM_ID);
				systemsUserIsSubscribedTo.add(systemId);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return systemsUserIsSubscribedTo;
	}

	public static User getSingleUser(ResultSet resultSet, List<Long> systemsUserIsSubscribedTo) {

		User user = null;

		try {

			if (resultSet.next()) {
				user = getUserFromResultSet(resultSet, systemsUserIsSubscribedTo);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return user;
	}
	
	public static List<User> getMultipleUsersNoSystems(ResultSet resultSet) {
		
		List<User> users = new ArrayList<User>();
		List<Long> systemsUserIsSubscribedTo = new ArrayList<Long>();
		
		try {
			
			while (resultSet.next()) {
				
				users.add(getUserFromResultSet(resultSet, systemsUserIsSubscribedTo));
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return users;
	}
}