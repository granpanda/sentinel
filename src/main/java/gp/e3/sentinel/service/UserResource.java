package gp.e3.sentinel.service;

import gp.e3.sentinel.domain.business.UserBusiness;
import gp.e3.sentinel.domain.entities.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	
	private final UserBusiness userBusiness;

	public UserResource(UserBusiness userBusiness) {
		
		this.userBusiness = userBusiness;
	}
	
	@POST
	public Response createUser(User user) {
		
		Response response = null;
		
		boolean userWasCreated = userBusiness.createUser(user);
		
		if (userWasCreated) {
			
			response = Response.status(201).entity(userWasCreated).build();
			
		} else {
			
			response = Response.status(500).entity(userWasCreated).build();
		}
		
		return response;
	}
	
	@GET
	@Path("/{mail}")
	public Response getUserByMail(@PathParam("mail") String mail) {
		
		Response response = null;
		User userByMail = userBusiness.getUserByMail(mail);
		
		if (userByMail != null) {
			
			response = Response.status(200).entity(userByMail).build();
			
		} else {
			
			String errorMessage = "The user with mail: " + mail + " was not found";
			response = Response.status(500).entity(errorMessage).build();
		}
		
		return response;
	}
}