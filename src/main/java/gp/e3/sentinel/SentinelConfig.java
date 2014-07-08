package gp.e3.sentinel;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

import gp.e3.sentinel.infrastructure.MySQLConfig;

public class SentinelConfig extends Configuration {
	
	@NotNull
	@JsonProperty
	private MySQLConfig mySQLConfig;

	public MySQLConfig getMySQLConfig() {
		return mySQLConfig;
	}
}