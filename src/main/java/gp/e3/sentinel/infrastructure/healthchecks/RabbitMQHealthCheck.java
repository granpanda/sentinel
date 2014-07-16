package gp.e3.sentinel.infrastructure.healthchecks;

import com.rabbitmq.client.Connection;
import com.yammer.metrics.core.HealthCheck;

public class RabbitMQHealthCheck extends HealthCheck {
	
	private Connection rabbitConnection;
	
	public RabbitMQHealthCheck(String name, Connection rabbitConnection) {
		
		super(name);
		this.rabbitConnection = rabbitConnection;
	}

	@Override
	protected Result check() throws Exception {
		
		return rabbitConnection.isOpen()? Result.healthy() : Result.unhealthy("The RabbitMQ connection is not open.");
	}
}