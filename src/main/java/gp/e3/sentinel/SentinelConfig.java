package gp.e3.sentinel;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

import gp.e3.sentinel.infrastructure.config.MySQLConfig;
import gp.e3.sentinel.infrastructure.config.RedisConfig;

public class SentinelConfig extends Configuration {
	
	@NotNull
	@JsonProperty
	private MySQLConfig mySQLConfig;
	
	@NotNull
	@JsonProperty
	private RedisConfig redisConfig;

	public MySQLConfig getMySQLConfig() {
		return mySQLConfig;
	}

	public RedisConfig getRedisConfig() {
		return redisConfig;
	}
}