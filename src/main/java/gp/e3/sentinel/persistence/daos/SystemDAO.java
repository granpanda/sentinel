package gp.e3.sentinel.persistence.daos;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;
import gp.e3.sentinel.persistence.mappers.SystemMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SystemDAO {
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String URL = "url";
	
	public int createSystemsTableIfDoesNotExist(Connection dbConnection) {
		
		int result = 0;
		String createSystemsTableIfDoesNotExistSQL = "CREATE TABLE IF NOT EXISTS systems ("
				+ "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
				+ "name VARCHAR(32) NOT NULL,"
				+ "url VARCHAR(256) NOT NULL);";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(createSystemsTableIfDoesNotExistSQL);
			result = prepareStatement.executeUpdate();
			prepareStatement.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public long createSystem(Connection dbConnection, System system) {
		
		long systemId = 0;
		String createSystemSQL = "INSERT INTO systems (name, url) VALUES (?, ?);";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(createSystemSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			prepareStatement.setString(1, system.getName());
			prepareStatement.setString(2, system.getUrl());
			
			prepareStatement.executeUpdate();
			systemId = SqlUtils.getGeneratedIdFromResultSet(prepareStatement.getGeneratedKeys());
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return systemId;
	}
	
	public List<System> getAllSystems(Connection dbConnection) {
		
		List<System> systems = new ArrayList<System>();
		String getAllSystemsSQL = "SELECT * FROM systems;";
		
		try {
			
			PreparedStatement prepareStatement = dbConnection.prepareStatement(getAllSystemsSQL);
			ResultSet resultSet = prepareStatement.executeQuery();
			systems = SystemMapper.getMultipleSystems(resultSet);
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return systems;
	}
}