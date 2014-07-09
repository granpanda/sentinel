package gp.e3.sentinel.domain.repositories;

import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.persistence.daos.RequestDAO;

import java.sql.Connection;

public class RequestRepository {
	
	private final RequestDAO requestDAO;

	public RequestRepository(RequestDAO failureDAO) {
		
		this.requestDAO = failureDAO;
	}
	
	public boolean createRequest(Connection dbConnection, Request request) {
		
		return (requestDAO.createRequest(dbConnection, request) == 1);
	}
}