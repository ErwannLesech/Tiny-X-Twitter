package com.epita.controller;

import com.epita.service.HomeTimelineService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

/**
 * REST controller to retrieve home timeline.
 */
@Path("/api")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HomeTimelineController {
    @Inject
    HomeTimelineService homeService;

    /**
     * Retrieves the home timeline for a given user.
     * @param userId User ID
     * @return A list of posts or a 400 error if userId is invalid, 404 error if user is unknown.
     */
    @GET
    @Path("/timeline/home/{userId}")
    public Response getTimeline(@PathParam("userId") final ObjectId userId) {
        return homeService.getHomeTimeline(userId);
    }
}