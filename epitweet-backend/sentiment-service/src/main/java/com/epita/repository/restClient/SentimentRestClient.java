package com.epita.repository.restClient;

import com.epita.contracts.sentiment.SentimentResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;


/**
 * REST client for interacting with the User Service.
 */
@RegisterRestClient()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SentimentRestClient {

    @POST
    @Path("/analyse")
    public RestResponse<SentimentResponse> analyse(String content);
}
