package com.epita.repository.restClients;

import com.epita.contracts.social.BlockedRelationRequest;
import com.epita.contracts.social.BlockedRelationResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SocialRestClient {

    @GET
    @Path("/getBlockedRelation")
    public RestResponse<BlockedRelationResponse> getBlockedRelation(BlockedRelationRequest blockedRelationRequest);

    @GET
    @Path("/getFollowers/{userId}")
    public RestResponse<List<String>> getFollowers(@PathParam("userId") String userId);
}
