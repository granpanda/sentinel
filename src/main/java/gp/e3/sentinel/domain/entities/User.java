package gp.e3.sentinel.domain.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	
	private final String mail;
	private final String fullName;
	private final List<Long> systemsWhichIsSubscribedTo;
	
	@JsonCreator
	public User(@JsonProperty("mail") String mail, @JsonProperty("fullName") String fullName, 
			@JsonProperty("systemsWhichIsSubscribedTo") List<Long> systemsWhichIsSubscribedTo) {
		
		this.mail = mail;
		this.fullName = fullName;
		this.systemsWhichIsSubscribedTo = systemsWhichIsSubscribedTo;
	}

	public String getMail() {
		return mail;
	}

	public String getFullName() {
		return fullName;
	}

	public List<Long> getSystemsWhichIsSubscribedTo() {
		return systemsWhichIsSubscribedTo;
	}
}