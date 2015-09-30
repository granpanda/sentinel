package gp.e3.sentinel;

import com.fasterxml.jackson.annotation.JsonProperty;
import gp.e3.sentinel.infrastructure.config.MySQLConfig;
import gp.e3.sentinel.infrastructure.config.RedisConfig;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

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