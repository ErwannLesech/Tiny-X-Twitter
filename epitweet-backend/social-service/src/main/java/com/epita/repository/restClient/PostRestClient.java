package com.epita.repository.restClient;

import com.epita.contracts.post.PostResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

/**
 * REST client for interacting with the repo-post.
 */
@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PostRestClient {

    @GET
    @Path("/getPost/{postId}")
    public RestResponse<PostResponse> getPost(@PathParam("postId") ObjectId postId);
}
