package gp.e3.sentinel.infrastructure.config;

import javax.validation.constraints.NotNull;

import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MySQLConfig extends Configuration {

	@NotNull
	@NotEmpty
	@JsonProperty
	private String driverClass;
	
	@NotNull
	@NotEmpty
	@JsonProperty
	private String url;
	
	@NotNull
	@NotEmpty
	@JsonProperty
	private String username;
	
	@NotNull
	@NotEmpty
	@JsonProperty
	private String password;
	
	@NotNull
    @NotEmpty
    @JsonProperty
    private int removeAbandonedTimeoutInSeconds;

    @NotNull
    @NotEmpty
    @JsonProperty
    private boolean ableToRemoveAbandonedConnections; 

	public String getDriverClass() {
		return driverClass;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getRemoveAbandonedTimeoutInSeconds() {
		return removeAbandonedTimeoutInSeconds;
	}

	public boolean isAbleToRemoveAbandonedConnections() {
		return ableToRemoveAbandonedConnections;
	}
}