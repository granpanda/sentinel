package gp.e3.sentinel.domain.entities;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class System implements Comparable<System> {
	
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
	
	public static boolean isValidSystem(System system) {
		
		return (system != null) && (system.getId() > 0) && (!StringUtils.isBlank(system.getName())) && (!StringUtils.isBlank(system.getUrl()));
	}

	@Override
	public int compareTo(System system) {
		
		int answer = 0;
		
		if (system != null && isValidSystem(system)) {
			
			answer += (id == system.getId()? 0 : 1);
			answer += name.compareToIgnoreCase(system.getName());
			answer += url.compareToIgnoreCase(system.getUrl());
			
		} else {
			
			answer = 1;
		}
		
		return answer;
	}
}