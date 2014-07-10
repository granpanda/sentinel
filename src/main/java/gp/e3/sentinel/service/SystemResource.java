package gp.e3.sentinel.service;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.entities.System;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/systems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SystemResource {
	
	private final SystemBusiness systemBusiness;

	public SystemResource(SystemBusiness systemBusiness) {
		
		this.systemBusiness = systemBusiness;
	}
	
	@POST
	public Response createSystem(System system) {
		
		Response response = null;
		long systemId = systemBusiness.createSystem(system);
		boolean systemWasCreated = systemId != 0;
		
		if (systemWasCreated) {
			
			response = Response.status(201).entity(systemWasCreated).build();
			
		} else {
			
			response = Response.status(500).entity(systemWasCreated).build();
		}
		
		return response;
	}
}