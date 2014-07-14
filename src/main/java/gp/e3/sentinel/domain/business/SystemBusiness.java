package gp.e3.sentinel.domain.business;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.jobs.CheckAllSystemsJob;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import redis.clients.jedis.JedisPool;

public class SystemBusiness {
	
	private final BasicDataSource dataSource;
	private final JedisPool redisPool;
	private final SystemRepository systemRepository;
	
	public SystemBusiness(BasicDataSource dataSource, JedisPool redisPool, SystemRepository systemRepository) {
		
		this.dataSource = dataSource;
		this.redisPool = redisPool;
		this.systemRepository = systemRepository;
	}

	public long createSystem(System system) {
		
		long systemId = 0;
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			systemId = systemRepository.createSystem(dbConnection, system);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return systemId;
	}
	
	public List<System> getAllSystems() {
		
		List<System> systems = new ArrayList<System>();
		Connection dbConnection = null;
		
		try {
			
			dbConnection = dataSource.getConnection();
			systems = systemRepository.getAllSystems(dbConnection);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
		
		return systems;
	}
	
	public void executeCheckAllSystemsJobForever() {
		
		try {
			
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			
			JobDetail jobDetail = JobBuilder.newJob(CheckAllSystemsJob.class)
					.withIdentity("checkAllSystemsJob")
					.build();
			
			int intervalInSeconds = 60;
			SimpleScheduleBuilder triggerSchedule = SimpleScheduleBuilder.simpleSchedule()
					.withIntervalInSeconds(intervalInSeconds)
					.repeatForever();
			
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("checkAllSystemsJobTrigger")
					.withSchedule(triggerSchedule)
					.startNow()
					.build();
			
			scheduler.start();
			scheduler.scheduleJob(jobDetail, trigger);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}