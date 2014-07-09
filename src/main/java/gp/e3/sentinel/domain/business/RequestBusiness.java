package gp.e3.sentinel.domain.business;

import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.domain.repositories.RequestRepository;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class RequestBusiness {
	
	private final BasicDataSource dataSource;
	private final RequestRepository requestRepository;

	public RequestBusiness(BasicDataSource dataSource, RequestRepository requestRepository) {
		
		this.dataSource = dataSource;
		this.requestRepository = requestRepository;
	}

	public boolean createFailure(Request request) {
		
		boolean failureWasCreated = false;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			failureWasCreated = requestRepository.createRequest(dbConnection, request);
			dbConnection.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return failureWasCreated;
	}
}