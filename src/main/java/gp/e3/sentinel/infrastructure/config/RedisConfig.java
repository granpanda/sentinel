package gp.e3.sentinel.infrastructure.config;

import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RedisConfig extends Configuration {
	
	@NotNull
	@NotEmpty
    @JsonProperty
	private String host;
	
	@NotNull
	@NotEmpty
    @JsonProperty
	private int port;
	
	@NotNull
	@NotEmpty
    @JsonProperty
	private int sentinelDatabase;

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getSentinelDatabase() {
		return sentinelDatabase;
	}
}