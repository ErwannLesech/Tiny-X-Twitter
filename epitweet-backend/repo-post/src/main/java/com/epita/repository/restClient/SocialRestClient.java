package com.epita.repository.restClient;

import com.epita.contracts.social.BlockedRelationRequest;
import com.epita.contracts.social.BlockedRelationResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;


/**
 * REST client for interacting with the User Service.
 */
@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SocialRestClient {

    @GET
    @Path("/getBlockedRelation")
    public RestResponse<BlockedRelationResponse> getBlockedRelation(BlockedRelationRequest blockedRelationRequest);
}
