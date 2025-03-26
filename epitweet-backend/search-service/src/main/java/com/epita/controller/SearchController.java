package com.epita.controller;

import com.epita.controller.contracts.PostRequest;
import com.epita.service.SearchService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchController {

    @Inject
    SearchService searchService;

    /**
     * Search posts based on the given PostRequest.
     *
     * @param request the search request.
     * @return a list of matching posts or appropriate error status.
     */
    @POST
    @Path("/searchPosts")
    public Response searchPosts(String request) {
        try {
            List<PostRequest> results = searchService.searchPosts(request);
            if (results.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No results found").build();
            }
            return Response.ok(results).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("An error occurred: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint to add a new post to Elasticsearch.
     *
     * @param request the PostRequest object containing post data.
     * @return 200 OK if the post is added successfully.
     *         400 BAD REQUEST if an error occurs during processing.
     */
    @POST
    @Path("/addPost")
    public Response addPost(PostRequest request) {
        try {
            searchService.indexPost(request);
            return Response.ok().entity("Post added successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Failed to add post: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Endpoint to delete a post from Elasticsearch by post ID.
     *
     * @param request the ID of the post to delete.
     * @return 200 OK if the post is deleted successfully.
     *         404 NOT FOUND if the post does not exist.
     *         400 BAD REQUEST if an error occurs during processing.
     */

    @POST
    @Path("/deletePost")
    public Response deletePost(PostRequest request) {
        try {
            searchService.deletePost(request.getParentId());
            return Response.ok().entity("Post deleted successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Failed to delete post: " + e.getMessage())
                    .build();
        }
    }
}
