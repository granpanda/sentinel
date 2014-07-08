package gp.e3.sentinel.persistence.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.persistence.daos.SystemDAO;

public class SystemMapper {
	
	private static System getSystemFromResultSet(ResultSet resultSet) {
		
		System system = null;
		
		try {
			
			long id = resultSet.getLong(SystemDAO.ID);
			String name = resultSet.getString(SystemDAO.NAME);
			String url = resultSet.getString(SystemDAO.URL);
			system = new System(id, name, url);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return system;
	}
	
	public static System getSingleSystem(ResultSet resultSet) {
		
		System system = null;
		
		try {
			
			if (resultSet.next()) {
				system = getSystemFromResultSet(resultSet);
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return system;
	}
	
	public static List<System> getMultipleSystems(ResultSet resultSet) {

		List<System> systems = new ArrayList<System>();
		
		try {
			
			while (resultSet.next()) {
				systems.add(getSystemFromResultSet(resultSet));
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return systems;
	}
}