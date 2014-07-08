package gp.e3.sentinel.infrastructure;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MySQLConfig {

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
}