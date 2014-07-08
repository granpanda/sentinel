package gp.e3.sentinel.domain.entities;

import org.joda.time.DateTime;

public class Failure {
	
	private final long id;
	private final String systemName;
	private final String systemUrl;
	private final DateTime failureDate;
	private final String failureCause;
	private final String message;
	
	public Failure(long id, String systemName, String systemUrl, DateTime failureDate, String failureCause, String message) {

		this.id = id;
		this.systemName = systemName;
		this.systemUrl = systemUrl;
		this.failureDate = failureDate;
		this.failureCause = failureCause;
		this.message = message;
	}

	public long getId() {
		return id;
	}

	public String getSystemName() {
		return systemName;
	}

	public String getSystemUrl() {
		return systemUrl;
	}

	public DateTime getFailureDate() {
		return failureDate;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public String getMessage() {
		return message;
	}
}