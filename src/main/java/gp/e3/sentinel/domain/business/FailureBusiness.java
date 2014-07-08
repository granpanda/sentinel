package gp.e3.sentinel.domain.business;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import gp.e3.sentinel.domain.entities.Failure;
import gp.e3.sentinel.domain.repositories.FailureRepository;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

public class FailureBusiness {
	
	private final BasicDataSource dataSource;
	private final FailureRepository failureRepository;

	public FailureBusiness(BasicDataSource dataSource, FailureRepository failureRepository) {
		
		this.dataSource = dataSource;
		this.failureRepository = failureRepository;
	}

	public boolean createFailure(Failure failure) {
		
		boolean failureWasCreated = false;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			failureWasCreated = failureRepository.createFailure(dbConnection, failure);
			dbConnection.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return failureWasCreated;
	}
}