package gp.e3.sentinel.domain.business;

import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.domain.repositories.RequestRepository;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

import java.sql.Connection;

import org.apache.commons.dbcp2.BasicDataSource;

public class RequestBusiness {
	
	private final BasicDataSource dataSource;
	private final RequestRepository requestRepository;

	public RequestBusiness(BasicDataSource dataSource, RequestRepository requestRepository) {
		
		this.dataSource = dataSource;
		this.requestRepository = requestRepository;
	}

	public long createRequest(Request request) {
		
		long requestId = 0;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			requestId = requestRepository.createRequest(dbConnection, request);
			dbConnection.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return requestId;
	}
}