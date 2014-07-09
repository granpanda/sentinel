package gp.e3.sentinel.domain.business;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.jobs.CheckAllSystemsJob;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.infrastructure.mq.RabbitHandler;
import gp.e3.sentinel.infrastructure.utils.DateTimeTypeConverter;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.joda.time.DateTime;
import org.quartz.JobDataMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;

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
	
	private Gson getDefaultGson() {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter());

		return gsonBuilder.create();
	}
	
	public void initializeCheckAllSystemsJob() {
		
		String host = "localhost";
		
		try {
			
			Gson gson = getDefaultGson();
			
			com.rabbitmq.client.Connection rabbitConnection = RabbitHandler.getRabbitConnection(host);
			Channel rabbitChannel = RabbitHandler.getRabbitChannelAndInitializeQueue(rabbitConnection, CheckAllSystemsJob.QUEUE_NAME);
			
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("gson", gson);
			jobDataMap.put("rabbitChannel", rabbitChannel);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}