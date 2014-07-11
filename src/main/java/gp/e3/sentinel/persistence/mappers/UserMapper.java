package gp.e3.sentinel.persistence.mappers;

import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.persistence.daos.UserDAO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {

	private static User getUserFromResultSet(ResultSet resultSet) {

		User user = null;

		try {

			String mail = resultSet.getString(UserDAO.MAIL);
			String fullName = resultSet.getString(UserDAO.FULL_NAME);
			List<Long> systemsWhichIsSubscribedTo = new ArrayList<Long>();

			user = new User(mail, fullName, systemsWhichIsSubscribedTo);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return user;
	}
	
	public static User getSingleUser(ResultSet resultSet) {
		
		User user = null;
		
		try {
			
			if (resultSet.next()) {
				user = getUserFromResultSet(resultSet);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return user;
	}
}