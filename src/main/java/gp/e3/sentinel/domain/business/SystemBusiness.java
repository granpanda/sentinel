package gp.e3.sentinel.domain.business;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

import org.apache.commons.dbcp2.BasicDataSource;

public class SystemBusiness {
	
	private final BasicDataSource dataSource;
	private final SystemRepository systemRepository;
	
	public SystemBusiness(BasicDataSource dataSource, SystemRepository systemRepository) {
		
		this.dataSource = dataSource;
		this.systemRepository = systemRepository;
	}
	
	public boolean createSystem(System system) {
		
		boolean systemWasCreated = false;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			systemWasCreated = systemRepository.createSystem(dbConnection, system);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return systemWasCreated;
	}
	
	public List<System> getAllSystems() {
		
		List<System> systems = new ArrayList<System>();
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			systems = systemRepository.getAllSystems(dbConnection);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return systems;
	}
}