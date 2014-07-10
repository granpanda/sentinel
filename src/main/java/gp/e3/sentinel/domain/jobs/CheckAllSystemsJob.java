package gp.e3.sentinel.domain.jobs;

import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.repositories.SystemRepository;
import gp.e3.sentinel.infrastructure.mq.RabbitHandler;
import gp.e3.sentinel.infrastructure.utils.JsonUtils;
import gp.e3.sentinel.infrastructure.utils.SqlUtils;
import gp.e3.sentinel.persistence.daos.SystemDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

@DisallowConcurrentExecution
public class CheckAllSystemsJob implements Job {

	public static final String MQ_HOST = "localhost";
	public static final String QUEUE_NAME = "sentinel_checkSystemsQueue";

	private Gson gson;
	private com.rabbitmq.client.Connection rabbitConnection;
	private Channel rabbitChannel;

	private Connection dbConnection;

	private SystemRepository systemRepository;

	private void initializeRabbitConnectionAndChannelIfNeeded() {

		try {

			if (rabbitConnection == null || !rabbitConnection.isOpen()) {

				RabbitHandler.closeChannel(rabbitChannel);
				rabbitConnection = RabbitHandler.getRabbitConnection(MQ_HOST);
			}

			if (rabbitChannel == null || !rabbitChannel.isOpen()) {

				rabbitChannel = RabbitHandler.getRabbitChannelAndInitializeQueue(rabbitConnection, QUEUE_NAME);
			}

		} catch (IOException e) {

			e.printStackTrace();
			RabbitHandler.closeRabbitConnection(rabbitConnection, rabbitChannel);
		}
	}
	
	private void getInitializedDbConnectionIfNeeded() throws ClassNotFoundException, SQLException {
		
		if (dbConnection == null || dbConnection.isClosed()) {
			
			Class.forName("com.mysql.jdbc.Driver");
			dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/sentineldb", "sentinel", "sentinel12345");
		}
	}

	private boolean initializeAttributes(JobExecutionContext context) {

		boolean allAttributesWereSuccessfullyInitialized = false;
		
		try {
			
			gson = JsonUtils.getDefaultGson();
			initializeRabbitConnectionAndChannelIfNeeded();
			getInitializedDbConnectionIfNeeded();
			initializeSystemRepository();

			boolean rabbitConnectionIsValid = (rabbitConnection != null && rabbitConnection.isOpen());
			boolean rabbitChannelIsValid = (rabbitChannel != null && rabbitChannel.isOpen());
			boolean dbConnectionIsValid = (dbConnection != null && !dbConnection.isClosed());
			
			allAttributesWereSuccessfullyInitialized = rabbitConnectionIsValid && rabbitChannelIsValid && dbConnectionIsValid;
			
		} catch (SQLException | ClassNotFoundException e) {
			
			e.printStackTrace();
			allAttributesWereSuccessfullyInitialized = false;
		}

		return allAttributesWereSuccessfullyInitialized;
	}

	private void initializeSystemRepository() {
		SystemDAO systemDAO = new SystemDAO();
		systemRepository = new SystemRepository(systemDAO);
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

		} finally {

			SqlUtils.closeDbConnection(dbConnection);
			RabbitHandler.closeRabbitConnection(rabbitConnection, rabbitChannel);
		}
	}
}