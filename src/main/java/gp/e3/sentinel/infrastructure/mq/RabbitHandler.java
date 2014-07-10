package gp.e3.sentinel.infrastructure.mq;

import java.io.IOException;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class RabbitHandler {

	public static Connection getRabbitConnection(String host) throws IOException {

		System.out.println("Opeing a rabbit connection to the host: " + host);

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setAutomaticRecoveryEnabled(true);

		return factory.newConnection();
	}

	public static Channel getRabbitChannelAndInitializeQueue(Connection connection, String queueName) throws IOException {

		Channel channel = connection.createChannel();

		boolean durable = true;
		boolean exclusive = false;
		boolean autoDelete = false;
		Map<String, Object> additionalArguments = null;

		System.out.println("The queue: "  + queueName + " has been initialized.");

		channel.queueDeclare(queueName, durable, exclusive, autoDelete, additionalArguments);

		return channel;
	}

	public static void publishMessageOnAGivenQueue(Channel channel, String routingKey, String message) throws IOException {

		String exchange = "";
		AMQP.BasicProperties properties = null;
		channel.basicPublish(exchange, routingKey, properties, message.getBytes());
		System.out.println("The following message has been published: " + message + "; in the queue: " + routingKey);
	}
	
	public static void acknowledgeMessage(Channel channel, Delivery delivery) {

		try {
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			System.out.println("The following delivery has been acknowledged: " + delivery.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeChannel(Channel channel) {

		System.out.println("Closing channel.");

		if (channel != null && channel.isOpen()) {

			try {
				
				channel.close();
				
			} catch (IOException e) {
				
				channel = null;
				e.printStackTrace();
				System.out.println("ERROR: Closing channel only, error message. Exception: " + e.getMessage());
			}
		}
		
		channel = null;
	}

	public static void closeRabbitConnection(Connection connection, Channel channel) {

		System.out.println("Closing connection and channel.");

		if (channel != null && channel.isOpen()) {

			try {

				channel.close();

			} catch (IOException e) {

				channel = null;
				e.printStackTrace();
				System.out.println("ERROR: Closing connection and channel, error message. Exception: " + e.getMessage());
			}
		}

		if (connection != null && connection.isOpen()) {

			try {

				connection.close();

			} catch (IOException e) {

				connection = null;
				e.printStackTrace();
				System.out.println("ERROR: Closing connection and channel, error message. Exception: " + e.getMessage());
			}
		}
		
		channel = null;
		connection = null;
	}
}