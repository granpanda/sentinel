package gp.e3.sentinel.domain.business;

import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.domain.repositories.RequestRepository;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

import java.sql.Connection;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBusiness {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestBusiness.class);
	
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

			LOGGER.error("createRequest", e);
			throw new IllegalStateException(e);
			
		} finally {

			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return requestId;
	}
}