package gp.e3.sentinel.persistence.daos;

import gp.e3.sentinel.domain.entities.User;
import gp.e3.sentinel.persistence.mappers.UserMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

	public static final String MAIL = "mail";
	public static final String FULL_NAME = "fullName";
	public static final String SYSTEMS_USER_IS_SUBSCRIBED_TO = "systemsUserIsSubscribedTo";

	public static final String SYSTEM_ID = "systemId";
	public static final String USER_MAIL = "userMail";

	private int createUsersTableTableIfDoesNotExist(Connection dbConnection) {

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

	private int createSystemsAndUsersTableTableIfDoesNotExist(Connection dbConnection) {

		int result = 0;
		String createUsersTableTableIfDoesNotExistSQL = "CREATE TABLE IF NOT EXISTS systems_users "
				+ "(systemId BIGINT, userMail VARCHAR(128), "
				+ "PRIMARY KEY (systemId, userMail), "
				+ "FOREIGN KEY (systemId) REFERENCES systems (id), "
				+ "FOREIGN KEY (mail) REFERENCES users (mail));";

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(createUsersTableTableIfDoesNotExistSQL);
			result = prepareStatement.executeUpdate();
			prepareStatement.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return result;
	}

	public int createUsersTablesIfNeeded(Connection dbConnection) {

		int result = 0;

		int createUsersTableTableIfDoesNotExistResult = createUsersTableTableIfDoesNotExist(dbConnection);
		int createSystemsAndUsersTableTableIfDoesNotExistResult = createSystemsAndUsersTableTableIfDoesNotExist(dbConnection);

		if (createUsersTableTableIfDoesNotExistResult + createSystemsAndUsersTableTableIfDoesNotExistResult == 2) {
			result = 1;
		}

		return result;
	}

	private int addAllArrayNumbers(int[] intArray) {

		int result = 0;

		for (int item : intArray) {

			result += item;
		}

		return result;
	}

	private int createUserIntoUsersTable(Connection dbConnection, User user) {

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

	private int subscribeUserToSystems(Connection dbConnection, String userMail, List<Long> systemIdList) {

		int result = 0;
		String subscribeUserToSystemsSQL = "INSERT INTO systems_users (systemId, userMail) VALUES (?, ?);";

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(subscribeUserToSystemsSQL);

			for (Long systemId : systemIdList) {

				prepareStatement.setLong(1, systemId);
				prepareStatement.setString(2, userMail);
				prepareStatement.addBatch();
			}

			int[] batchResult = prepareStatement.executeBatch();
			result = addAllArrayNumbers(batchResult);
			prepareStatement.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return result;
	}

	public int createUser(Connection dbConnection, User user) {

		int createUserIntoUsersTableResult = createUserIntoUsersTable(dbConnection, user);
		int systemsUserIsSubscribedTo = subscribeUserToSystems(dbConnection, user.getMail(), user.getSystemsUserIsSubscribedTo());

		int result = 0;

		if (createUserIntoUsersTableResult == 1 && systemsUserIsSubscribedTo != 0) {
			result = 1;
		}

		return result;
	}

	private List<Long> getSystemsUserIsSubscribedTo(Connection dbConnection, String userMail) {

		List<Long> systemsUserIsSubscribedTo = new ArrayList<Long>();
		String getSystemsUserIsSubscribedToSQL = "SELECT systemId FROM systems_users WHERE userMail = ?;";

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(getSystemsUserIsSubscribedToSQL);
			prepareStatement.setString(1, userMail);

			ResultSet resultSet = prepareStatement.executeQuery();
			systemsUserIsSubscribedTo = UserMapper.getSystemsUserIsSubscribedToFromResultSet(resultSet);

			resultSet.close();
			prepareStatement.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return systemsUserIsSubscribedTo;
	}

	public User getUserByMail(Connection dbConnection, String mail) {

		User retrievedUser = null;
		String getUserByMailSQL = "SELECT * FROM users WHERE mail = ?;";

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(getUserByMailSQL);
			prepareStatement.setString(1, mail);

			ResultSet resultSet = prepareStatement.executeQuery();
			List<Long> systemsUserIsSubscribedTo = getSystemsUserIsSubscribedTo(dbConnection, mail);

			retrievedUser = UserMapper.getSingleUser(resultSet, systemsUserIsSubscribedTo);

			resultSet.close();
			prepareStatement.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

		return retrievedUser;
	}

	public List<User> getAllUsersSubscribedToASystem(Connection dbConnection, long systemId) {

		List<User> users = new ArrayList<User>();

		String getUsersSubscribedToASystemSQL = "SELECT * FROM users LEFT JOIN systems_users ON users.mail = systems_users.userMail WHERE (systems_users.systemId = ?);";

		try {

			PreparedStatement prepareStatement = dbConnection.prepareStatement(getUsersSubscribedToASystemSQL);
			prepareStatement.setLong(1, systemId);

			ResultSet resultSet = prepareStatement.executeQuery();
			users = UserMapper.getMultipleUsersNoSystems(resultSet);

			resultSet.close();
			prepareStatement.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return users;
	}
}