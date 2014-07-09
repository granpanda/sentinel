package gp.e3.sentinel.domain.entities;

import org.joda.time.DateTime;

public class Request {
	
	private final long id;
	
	private final long systemId;
	private final String systemName;
	private final String systemUrl;
	
	private final int httpResponseStatusCode;
	private final String httpResponseEntity;
	
	private final DateTime requestExecutionDate;
	private final long requestExecutionTimeInMilliseconds;
	
	public Request(long id, long systemId, String systemName, String systemUrl, int httpResponseStatusCode, 
			String httpResponseEntity, DateTime requestExecutionDate, long requestExecutionTimeInMilliseconds) {
		
		this.id = id;
		this.systemId = systemId;
		this.systemName = systemName;
		this.systemUrl = systemUrl;
		this.httpResponseStatusCode = httpResponseStatusCode;
		this.httpResponseEntity = httpResponseEntity;
		this.requestExecutionDate = requestExecutionDate;
		this.requestExecutionTimeInMilliseconds = requestExecutionTimeInMilliseconds;
	}

	public long getId() {
		return id;
	}

	public long getSystemId() {
		return systemId;
	}

	public String getSystemName() {
		return systemName;
	}

	public String getSystemUrl() {
		return systemUrl;
	}

	public int getHttpResponseStatusCode() {
		return httpResponseStatusCode;
	}

	public String getHttpResponseEntity() {
		return httpResponseEntity;
	}

	public DateTime getRequestExecutionDate() {
		return requestExecutionDate;
	}

	public long getRequestExecutionTimeInMilliseconds() {
		return requestExecutionTimeInMilliseconds;
	}
}