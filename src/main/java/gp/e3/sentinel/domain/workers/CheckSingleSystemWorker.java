package gp.e3.sentinel.domain.workers;

import gp.e3.sentinel.domain.jobs.CheckAllSystemsJob;
import gp.e3.sentinel.domain.repositories.RequestRepository;
import gp.e3.sentinel.infrastructure.mq.RabbitHandler;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

public class CheckSingleSystemWorker implements Runnable {

	public static final String QUEUE_HOST = CheckAllSystemsJob.QUEUE_HOST;
	public static final String QUEUE_NAME = CheckAllSystemsJob.QUEUE_NAME;

	private final Gson gson;

	private Connection rabbitConnection;
	private Channel rabbitChannel;

	private java.sql.Connection dbConnection;
	private final BasicDataSource dataSource;

	private final RequestRepository requestRepository;
	private final HttpClientBuilder httpClientBuilder;

	public CheckSingleSystemWorker(Gson gson, Connection rabbitConnection, Channel rabbitChannel, BasicDataSource dataSource, 
			RequestRepository requestRepository, HttpClientBuilder httpClientBuilder) {

		this.gson = gson;
		this.rabbitConnection = rabbitConnection;
		this.rabbitChannel = rabbitChannel;
		this.dataSource = dataSource;
		this.requestRepository = requestRepository;
		this.httpClientBuilder = httpClientBuilder;
	}

	private boolean initializeRabbitConnectionAndChannelIfNeeded(com.rabbitmq.client.Connection connection, Channel channel) {

		boolean connectionAndChannelAreOpen = false;

		try {

			if (connection == null || !connection.isOpen()) {

				RabbitHandler.closeChannel(channel);
				connection = RabbitHandler.getRabbitConnection(QUEUE_HOST);
			}

			if (channel == null || !channel.isOpen()) {

				channel = RabbitHandler.getRabbitChannelAndInitializeQueue(connection, QUEUE_NAME);
				int prefetchCount = 1;
				channel.basicQos(prefetchCount);
			}

			connectionAndChannelAreOpen = connection.isOpen() && channel.isOpen();

		} catch (IOException e) {

			e.printStackTrace();
			RabbitHandler.closeRabbitConnection(connection, channel);
			connectionAndChannelAreOpen = false;
		}

		return connectionAndChannelAreOpen;
	}

	private boolean initializeDbConnection(java.sql.Connection databaseConnection) {

		boolean dbConnectionIsOpen = false;

		try {

			if (databaseConnection == null || databaseConnection.isClosed()) {

				databaseConnection = dataSource.getConnection();
			}

			dbConnectionIsOpen = !databaseConnection.isClosed();

		} catch (SQLException e) {

			e.printStackTrace();
			dbConnectionIsOpen = false;
		}

		return dbConnectionIsOpen;
	}

	private boolean initializeMqAndDatabaseArtifactsIfNeeded() {

		boolean connectionAndChannelAreOpen = initializeRabbitConnectionAndChannelIfNeeded(rabbitConnection, rabbitChannel);
		boolean dbConnectionIsOpen = initializeDbConnection(dbConnection);

		return connectionAndChannelAreOpen && dbConnectionIsOpen;
	}

	@Override
	public void run() {

		QueueingConsumer consumer = null;

		while (true) {

			try {

				if (initializeMqAndDatabaseArtifactsIfNeeded()) {
					
					consumer = new QueueingConsumer(rabbitChannel);
					boolean autoAck = false;
					rabbitChannel.basicConsume(QUEUE_NAME, autoAck, consumer);
					
					
				}

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
}