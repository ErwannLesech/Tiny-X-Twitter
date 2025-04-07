package com.epita.repository.restClient;

import com.epita.contracts.user.UserResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

/**
 * REST client for interacting with the User Service.
 */
@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserRestClient {

    @GET
    @Path("/getUserById")
    public RestResponse<UserResponse> getUser(@HeaderParam("userId") ObjectId userId);
}