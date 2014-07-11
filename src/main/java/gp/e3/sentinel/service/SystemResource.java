package gp.e3.sentinel.service;

import gp.e3.sentinel.domain.business.SystemBusiness;
import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.entities.System;
import gp.e3.sentinel.domain.entities.User;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/systems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SystemResource {
	
	private final SystemBusiness systemBusiness;
	private final UserBusiness userBusiness;
	
	public SystemResource(SystemBusiness systemBusiness, UserBusiness userBusiness) {
		
		this.systemBusiness = systemBusiness;
		this.userBusiness = userBusiness;
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
	
	@GET
	@Path("/{systemId}/users")
	public Response getAllUsersSubscribedToASystem(@PathParam("systemId") long systemId) {
		
		List<User> users = userBusiness.getAllUsersSubscribedToASystem(systemId);
		return Response.status(200).entity(users).build();
	}
}