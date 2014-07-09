package gp.e3.sentinel.domain.jobs;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.infrastructure.mq.RabbitHandler;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class CheckAllSystemsJob implements Job {

	public static final String QUEUE_HOST = "localhost";
	public static final String QUEUE_NAME = "sentinel_checkSystemsQueue";

	private Gson gson;
	private com.rabbitmq.client.Connection rabbitConnection;
	private Channel rabbitChannel;

	private BasicDataSource dataSource;
	private Connection dbConnection;

	private SystemRepository systemRepository;

	private void initializeRabbitConnectionAndChannelIfNeeded(com.rabbitmq.client.Connection connection, Channel channel) {

		try {

			if (connection == null || !connection.isOpen()) {

				RabbitHandler.closeChannel(channel);
				connection = RabbitHandler.getRabbitConnection(QUEUE_HOST);
			}

			if (channel == null || !channel.isOpen()) {

				channel = RabbitHandler.getRabbitChannelAndInitializeQueue(connection, QUEUE_NAME);
			}

		} catch (IOException e) {

			e.printStackTrace();
			RabbitHandler.closeRabbitConnection(connection, channel);
		}
	}

	private Connection getConnection(BasicDataSource dataSource) {

		Connection dbConnection = null;

		try {
			dbConnection = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dbConnection;
	}

	private boolean initializeAttributes(JobExecutionContext context) {

		gson = (Gson) context.get("gson");

		rabbitConnection = (com.rabbitmq.client.Connection) context.get("rabbitConnection");
		rabbitChannel = (Channel) context.get("rabbitChannel");
		initializeRabbitConnectionAndChannelIfNeeded(rabbitConnection, rabbitChannel);

		dataSource = (BasicDataSource) context.get("dataSource");
		dbConnection = getConnection(dataSource);
		systemRepository = (SystemRepository) context.get("systemRepository");
		
		boolean rabbitConnectionIsValid = (rabbitConnection != null && rabbitConnection.isOpen());
		boolean rabbitChannelIsValid = (rabbitChannel != null && rabbitChannel.isOpen());
		boolean dbConnectionIsValid = false;
		
		try {
			dbConnectionIsValid = (dbConnection != null && !dbConnection.isClosed());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rabbitConnectionIsValid && rabbitChannelIsValid && dbConnectionIsValid;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		boolean allAttributesWereSuccessfullyInitialized = initializeAttributes(context);

		try {

			if (allAttributesWereSuccessfullyInitialized) {

				List<System> systems = systemRepository.getAllSystems(dbConnection);

				for (System system : systems) {

					String systemAsJson = gson.toJson(system);
					RabbitHandler.publishMessageOnAGivenQueue(rabbitChannel, QUEUE_NAME, systemAsJson);
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
			RabbitHandler.closeRabbitConnection(rabbitConnection, rabbitChannel);
			
		} finally {
			
			SqlUtils.closeDbConnection(dbConnection);
		}
	}
}