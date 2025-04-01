package com.epita.repository;

import com.epita.controller.contracts.PostResponse;
import com.epita.service.SearchService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.util.logging.Logger;

@ApplicationScoped
public class PostRestClient {
    private static final String POST_URL = "http://localhost:8082/api/posts";
    private final Client client;
    private static final Logger LOGGER = Logger.getLogger(PostRestClient.class.getName());

    public PostRestClient() {
        this.client = ClientBuilder.newClient();
    }

    public PostResponse getPost(String postId) {
        try {
            return client.target(POST_URL + "/getPost/"+ postId)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<PostResponse>() {});
        }catch (Exception e) {
            LOGGER.info(e.getMessage());
            return null;
        }
    }
}
