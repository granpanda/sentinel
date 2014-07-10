package gp.e3.sentinel.domain.repositories;

import java.sql.Connection;
import java.util.List;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.persistence.daos.SystemDAO;

public class SystemRepository {
	
	private final SystemDAO systemDAO;

	public SystemRepository(SystemDAO systemDAO) {
		
		this.systemDAO = systemDAO;
	}
	
	public long createSystem(Connection dbConnection, System system) {
		
		return systemDAO.createSystem(dbConnection, system);
	}
	
	public List<System> getAllSystems(Connection dbConnection) {
		
		return systemDAO.getAllSystems(dbConnection);
	}
}