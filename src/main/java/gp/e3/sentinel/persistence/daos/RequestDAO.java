package gp.e3.sentinel.persistence.daos;

import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.infrastructure.utils.DateUtils;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;
import gp.e3.sentinel.persistence.mappers.RequestMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestDAO {
	
	public static final String ID = "id";
	
	public static final String SYSTEM_ID = "systemId";
	public static final String SYSTEM_NAME = "systemName";
	public static final String SYSTEM_URL = "systemUrl";
	
	public static final String HTTP_RESPONSE_STATUS_CODE = "httpResponseStatusCode";
	public static final String HTTP_RESPONSE_ENTITY = "httpResponseEntity";
	
	public static final String REQUEST_EXECUTION_DATE = "requestExecutionDate";
	public static final String REQUEST_EXECUTION_TIME_IN_MILLISECONDS = "requestExecutionTimeInMilliseconds";
	
	public int createRequestsTableIfDoesNotExist(Connection dbConnection) {
		
		int result = 0;
		String createRequestsTableIfDoesNotExistSQL = "CREATE TABLE IF NOT EXISTS requests ("
				+ "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
				+ "systemId BIGINT NOT NULL, "
				+ "systemName VARCHAR(32) NOT NULL, "
				+ "systemUrl VARCHAR(512) NOT NULL, "
				+ "httpResponseStatusCode INT NOT NULL, "
				+ "httpResponseEntity VARCHAR(5120), "
				+ "requestExecutionDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
				+ "requestExecutionTimeInMilliseconds BIGINT NOT NULL);";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(createRequestsTableIfDoesNotExistSQL);
			result = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public long createRequest(Connection dbConnection, Request request) {
		
		long requestId = 0;
		// The request date is created automatically with the current time stamp.
		String createRequestSQL = "INSERT INTO requests (systemId, systemName, systemUrl, httpResponseStatusCode, httpResponseEntity, requestExecutionDate"
				+ "requestExecutionTimeInMilliseconds) VALUES (?, ?, ?, ?, ?, ?, ?);";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(createRequestSQL);
			prepareStatement.setLong(1, request.getSystemId());
			prepareStatement.setString(2, request.getSystemName());
			prepareStatement.setString(3, request.getSystemUrl());
			prepareStatement.setInt(4, request.getHttpResponseStatusCode());
			prepareStatement.setString(5, request.getHttpResponseEntity());
			prepareStatement.setTimestamp(6, DateUtils.getTimestampFromDateTime(request.getRequestExecutionDate()));
			prepareStatement.setLong(7, request.getRequestExecutionTimeInMilliseconds());
			
			prepareStatement.executeUpdate();
			requestId = SqlUtils.getGeneratedIdFromResultSet(prepareStatement.getGeneratedKeys());
			prepareStatement.close();
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return requestId;
	}
	
	public Request getRequestById(Connection dbConnection, long requestId) {
		
		Request request = null;
		String getRequestByIdSQL = "SELECT * FROM requests WHERE id = ?;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getRequestByIdSQL);
			prepareStatement.setLong(1, requestId);
			
			ResultSet resultSet = prepareStatement.executeQuery();
			request = RequestMapper.getSingleRequest(resultSet);
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return request;
	}
}