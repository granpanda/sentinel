package gp.e3.sentinel.persistence.daos;

import gp.e3.sentinel.domain.entities.Failure;
import gp.e3.sentinel.persistence.mappers.FailureMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FailureDAO {
	
	public static final String ID = "id";
	public static final String SYSTEM_NAME = "systemName";
	public static final String SYSTEM_URL = "systemUrl";
	public static final String FAILURE_DATE = "failureDate";
	public static final String FAILURE_CAUSE = "failureCause";
	public static final String MESSAGE = "message";
	
	public int createFailuresTableIfDoesNotExist(Connection dbConnection) {
		
		int result = 0;
		String createFailuresTableIfDoesNotExistSQL = "CREATE TABLE IF NOT EXISTS failures ("
				+ "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
				+ "systemName VARCHAR(32) NOT NULL,"
				+ "systemUrl VARCHAR(256) NOT NULL,"
				+ "failureDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
				+ "failureCause VARCHAR(64) NOT NULL,"
				+ "message VARCHAR(512));";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(createFailuresTableIfDoesNotExistSQL);
			result = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int createFailure(Connection dbConnection, Failure failure) {
		
		int affectedRows = 0;
		// The failure date is created automatically with the current time stamp.
		String createFaulireSQL = "INSERT INTO failures (systemName, systemUrl, failureCause, message) VALUES (?, ?, ?, ?)";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(createFaulireSQL);
			prepareStatement.setString(1, failure.getSystemName());
			prepareStatement.setString(2, failure.getSystemUrl());
			prepareStatement.setString(3, failure.getFailureCause());
			prepareStatement.setString(4, failure.getMessage());
			
			affectedRows = prepareStatement.executeUpdate();
			prepareStatement.close();
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return affectedRows;
	}
	
	public Failure getFailureById(Connection dbConnection, long failureId) {
		
		Failure failure = null;
		String getFailureByIdSQL = "SELECT * FROM failures WHERE id = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getFailureByIdSQL);
			prepareStatement.setLong(1, failureId);
			
			ResultSet resultSet = prepareStatement.executeQuery();
			failure = FailureMapper.getSingleFailure(resultSet);
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return failure;
	}
}