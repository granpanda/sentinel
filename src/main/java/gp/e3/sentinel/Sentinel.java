package gp.e3.sentinel;

import gp.e3.sentinel.infrastructure.MySQLConfig;

import org.apache.commons.dbcp2.BasicDataSource;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class Sentinel extends Service<SentinelConfig> {
	
	public static final String MAIN_URL = "http://localhost";

	public static void main(String[] args) {
		
		try {
			
			Sentinel sentinel = new Sentinel();
			sentinel.run(args);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(Bootstrap<SentinelConfig> bootstrap) {
		
	}
	
	private BasicDataSource getInitializedDataSource(MySQLConfig mySQLConfig) {
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(mySQLConfig.getDriverClass());
		dataSource.setUrl(mySQLConfig.getUrl());
		dataSource.setUsername(mySQLConfig.getUsername());
		dataSource.setPassword(mySQLConfig.getPassword());
		
		return dataSource;
	}

	@Override
	public void run(SentinelConfig configuration, Environment environment) throws Exception {
		
		BasicDataSource dataSource = getInitializedDataSource(configuration.getMySQLConfig());
	}
}