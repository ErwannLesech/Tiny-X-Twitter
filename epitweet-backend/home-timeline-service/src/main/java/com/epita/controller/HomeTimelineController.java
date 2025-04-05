package com.epita.controller;

import com.epita.service.HomeTimelineService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

@Path("/api")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HomeTimelineController {
    @Inject
    HomeTimelineService homeService;

    @GET
    @Path("/timeline/home/{userId}")
    public Response getTimeline(@PathParam("userId") final ObjectId userId) {
        return homeService.getTimeline(userId);
    }
}