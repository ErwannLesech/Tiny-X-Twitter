package com.epita.repository.restClients;

import com.epita.contracts.post.PostResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

/**
 * REST client for interacting with the Post Service.
 */
@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PostRestClient {

    /**
     * Retrieves a specific post by its ID.
     * @param postId Post ID
     * @return The post or a 404 error if not found.
     */
    @GET
    @Path("/getPost/{postId}")
    public RestResponse<PostResponse> getPost(@PathParam("postId") ObjectId postId);

    /**
     * Retrieves all posts for a given user.
     * @param userId User ID
     * @return A list of posts or a 400 error if userId is invalid.
     */
    @GET
    @Path("/getPosts/")
    public RestResponse<List<PostResponse>> getPosts(ObjectId userId);
}
