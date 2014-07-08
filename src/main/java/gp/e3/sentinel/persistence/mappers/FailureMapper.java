package gp.e3.sentinel.persistence.mappers;

import gp.e3.sentinel.domain.entities.Failure;
import gp.e3.sentinel.infrastructure.utils.DateUtils;
import gp.e3.sentinel.persistence.daos.FailureDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.joda.time.DateTime;

public class FailureMapper {

	private static Failure getFailureFromResultSet(ResultSet resultSet) {
		
		Failure failure = null;
		
		try {
			
			long id = resultSet.getLong(FailureDAO.ID);
			String systemName = resultSet.getString(FailureDAO.SYSTEM_NAME);
			String systemUrl = resultSet.getString(FailureDAO.SYSTEM_URL);
			
			Timestamp failureDateTimeStamp = resultSet.getTimestamp(FailureDAO.FAILURE_DATE);
			DateTime failureDate = DateUtils.getDateTimeFromTimeStamp(failureDateTimeStamp);
			
			String failureCause = resultSet.getString(FailureDAO.FAILURE_CAUSE);
			String message = resultSet.getString(FailureDAO.MESSAGE);
			
			failure = new Failure(id, systemName, systemUrl, failureDate, failureCause, message);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
		return failure;
	}
	
	public static Failure getSingleFailure(ResultSet resultSet) {
		
		Failure failure = null;
		
		try {
			
			if (resultSet.next()) {
				failure = getFailureFromResultSet(resultSet);
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return failure;
	}
}