package com.epita.controller;

import com.epita.controller.contracts.UserTimelineResponse;
import com.epita.service.UserTimelineService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * REST controller for user timeline.
 * Provides endpoint for retrieving specific user's timeline.
 */
@ApplicationScoped
@Path("/api/timeline/")
public class UserTimelineController {
    /**
     * Logger used to log HTTP request handling events.
     */
    @Inject
    Logger logger;

    /**
     * Service for retrieving and managing user timeline data.
     */
    @Inject
    UserTimelineService userTimelineService;

    /**
     * Retrieves user timeline for a given user.
     * @param userId User ID
     * @return A user timeline response including the user id and a list of posts.
     */
    @GET
    @Path("/user/{userId}")
    public Response getUserTimeline(@PathParam("userId") ObjectId userId) {
        logger.infof("getUserTimeline Request: %s", userId);
        UserTimelineResponse userTimeline= userTimelineService.getUserTimeline(userId);
        logger.debugf("getUserTimeline response 200: %s", userTimeline);
        return Response.ok(userTimeline).build(); // 200
    }
}
