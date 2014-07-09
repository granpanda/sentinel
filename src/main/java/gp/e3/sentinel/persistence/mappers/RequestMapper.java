package gp.e3.sentinel.persistence.mappers;

import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.infrastructure.utils.DateUtils;
import gp.e3.sentinel.persistence.daos.RequestDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.joda.time.DateTime;

public class RequestMapper {

	private static Request getRequestFromResultSet(ResultSet resultSet) {
		
		Request request = null;
		
		try {
			
			long id = resultSet.getLong(RequestDAO.ID);
			long systemId = resultSet.getLong(RequestDAO.SYSTEM_ID);
			String systemName = resultSet.getString(RequestDAO.SYSTEM_NAME);
			String systemUrl = resultSet.getString(RequestDAO.SYSTEM_URL);
			
			int httpResponseStatusCode = resultSet.getInt(RequestDAO.HTTP_RESPONSE_STATUS_CODE);
			String httpResponseEntity = resultSet.getString(RequestDAO.HTTP_RESPONSE_ENTITY);
			
			Timestamp requestExecutionDateAsTimeStamp = resultSet.getTimestamp(RequestDAO.REQUEST_EXECUTION_DATE);
			DateTime requestExecutionDate = DateUtils.getDateTimeFromTimeStamp(requestExecutionDateAsTimeStamp);
			long requestExecutionTimeInMilliseconds = resultSet.getLong(RequestDAO.REQUEST_EXECUTION_TIME_IN_MILLISECONDS);
			
			request = new Request(id, systemId, systemName, systemUrl, httpResponseStatusCode, httpResponseEntity, requestExecutionDate, 
					requestExecutionTimeInMilliseconds);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		return request;
	}
	
	public static Request getSingleRequest(ResultSet resultSet) {
		
		Request request = null;
		
		try {
			
			if (resultSet.next()) {
				request = getRequestFromResultSet(resultSet);
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return request;
	}
}