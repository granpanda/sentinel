package gp.e3.sentinel.domain.entities;

import java.util.List;

public class User {
	
	private final String mail;
	private final String fullName;
	private final List<Long> systemsWhichIsPartOf;
	
	public User(String mail, String fullName, List<Long> systemsWhichIsPartOf) {
		
		this.mail = mail;
		this.fullName = fullName;
		this.systemsWhichIsPartOf = systemsWhichIsPartOf;
	}

	public String getMail() {
		return mail;
	}

	public String getFullName() {
		return fullName;
	}

	public List<Long> getSystemsWhichIsPartOf() {
		return systemsWhichIsPartOf;
	}
}