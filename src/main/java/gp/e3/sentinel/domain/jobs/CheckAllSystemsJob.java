package gp.e3.sentinel.domain.jobs;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.infrastructure.mq.RabbitHandler;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

@DisallowConcurrentExecution
public class CheckAllSystemsJob implements Job {

	public static final String QUEUE_NAME = "sentinel_checkSystemsQueue";

	private Gson gson;
	private com.rabbitmq.client.Connection rabbitConnection;
	private Channel rabbitChannel;
	private Connection dbConnection;
	private SystemRepository systemRepository;
	
	private void initializeAttributes(JobExecutionContext context) {
		
		try {
			
			gson = (Gson) context.get("gson");
			
			rabbitConnection = (com.rabbitmq.client.Connection) context.get("rabbitConnection");
			rabbitChannel = RabbitHandler.getRabbitChannelAndInitializeQueue(rabbitConnection, QUEUE_NAME);
			
			dbConnection = (Connection) context.get("dbConnection");
			systemRepository = (SystemRepository) context.get("systemRepository");
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		initializeAttributes(context);
		
		try {
			
			List<System> systems = systemRepository.getAllSystems(dbConnection);

			for (System system : systems) {

				String systemAsJson = gson.toJson(system);
				RabbitHandler.publishMessageOnAGivenQueue(rabbitChannel, QUEUE_NAME, systemAsJson);
			}

		} catch (IOException e) {

			e.printStackTrace();
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
			RabbitHandler.closeRabbitConnection(rabbitConnection, rabbitChannel);
		}
	}
}