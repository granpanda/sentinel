package gp.e3.sentinel.util;

import org.joda.time.DateTime;

import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.domain.entities.System;

public class RequestFactoryForTests {
	
	public static Request getDefaultRequest() {
		
		long id = 1;
		
		System system = SystemFactoryForTests.getDefaultSystem();
		long systemId = system.getId();
		String systemName = system.getName();
		String systemUrl = system.getUrl();
		
		int httpResponseStatusCode = 200;
		String httpResponseEntity = "httpResponseEntity";
		
		DateTime requestExecutionDate = new DateTime("2014-07-10");
		long requestExecutionTimeInMilliseconds = 100;
		
		return new Request(id, systemId, systemName, systemUrl, httpResponseStatusCode, httpResponseEntity, requestExecutionDate, requestExecutionTimeInMilliseconds);
	}
}