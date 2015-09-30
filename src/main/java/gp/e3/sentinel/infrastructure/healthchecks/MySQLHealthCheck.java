package gp.e3.sentinel.infrastructure.healthchecks;

import com.codahale.metrics.health.HealthCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySQLHealthCheck extends HealthCheck {
	
	private Connection dbConnection;
	
	public MySQLHealthCheck(Connection dbConnection) {
		this.dbConnection = dbConnection;
	}

	@Override
	protected Result check() throws Exception {
		PreparedStatement prepareStatement = dbConnection.prepareStatement("SELECT 1;");
		ResultSet resultSet = prepareStatement.executeQuery();
		return resultSet.next()? Result.healthy() : Result.unhealthy("mysql failure.");
	}
}