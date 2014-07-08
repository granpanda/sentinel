package gp.e3.sentinel.domain.repositories;

import java.sql.Connection;

import gp.e3.sentinel.domain.entities.Failure;
import gp.e3.sentinel.persistence.daos.FailureDAO;

public class FailureRepository {
	
	private final FailureDAO failureDAO;

	public FailureRepository(FailureDAO failureDAO) {
		
		this.failureDAO = failureDAO;
	}
	
	public boolean createFailure(Connection dbConnection, Failure failure) {
		
		return (failureDAO.createFailure(dbConnection, failure) == 1);
	}
}