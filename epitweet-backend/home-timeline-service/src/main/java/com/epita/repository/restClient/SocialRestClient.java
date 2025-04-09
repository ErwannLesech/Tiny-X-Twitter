package com.epita.repository.restClient;

import com.epita.contracts.social.BlockedRelationRequest;
import com.epita.contracts.social.BlockedRelationResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

/**
 * REST client for interacting with the Social Service.
 */
@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SocialRestClient {

    /**
     * Checks if two users have mutually blocked each other.
     * @param blockedRelationRequest the request indicating which users to check
     * @return a Response containing a BlockedRelationResponse that indicates the blocked relation
     */
    @GET
    @Path("/getBlockedRelation")
    public RestResponse<BlockedRelationResponse> getBlockedRelation(BlockedRelationRequest blockedRelationRequest);

    /**
     * Gets the followers of a specific user.
     * @param userId the user for whom to get the followers
     * @return a Response containing a list of userIds who follow the specified userId
     */
    @GET
    @Path("/getFollowers/{userId}")
    public RestResponse<List<String>> getFollowers(@PathParam("userId") String userId);
}
