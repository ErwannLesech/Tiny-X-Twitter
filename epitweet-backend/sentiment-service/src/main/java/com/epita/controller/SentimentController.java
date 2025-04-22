package com.epita.controller;

import com.epita.contracts.sentiment.SentimentResponse;
import com.epita.service.SentimentService;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

/**
 * REST controller for managing sentiments of posts
 * Provides endpoints for retrieving post-sentiment.
 */
@ApplicationScoped
@Path("/api/sentiment/")
public class SentimentController {

    @Inject
    Logger logger;

    @Inject
    SentimentService sentimentService;

    /**
     * Retrieves post sentiment for a given postId
     * @param postId the post ID
     * @return A SentimentResponse.
     */
    @GET
    @Path("/getPostSentiment/{postId}")
    public Response getPostSentiment(@PathParam("postId") final String postId) {

        logger.infof("getPostSentiment Request: %s", postId);

        if (postId == null || postId.isEmpty() || !ObjectId.isValid(postId)) {
            logger.warn("getPostSentiment response 400 - postId is null or empty");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        SentimentResponse sentimentResponse = sentimentService.getPostSentiment(postId);

        if (sentimentResponse == null) {
            logger.warn("getPostSentiment response 404 - postId not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(sentimentResponse).build();
    }
}
