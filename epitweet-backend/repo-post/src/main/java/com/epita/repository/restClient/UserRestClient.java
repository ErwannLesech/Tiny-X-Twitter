package com.epita.repository.restClient;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


/**
 * REST client for interacting with the User Service.
 */
@ApplicationScoped
@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserRestClient {

    @GET
    @Path("/getUserById")
    public Response getUserById(@HeaderParam("userId") ObjectId userId);

}
