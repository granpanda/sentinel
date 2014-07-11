package gp.e3.sentinel.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class System {
	
	private final long id;
	private final String name;
	private final String url;
	
	@JsonCreator
	public System(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("url") String url) {
		
		this.id = id;
		this.name = name;
		this.url = url;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}