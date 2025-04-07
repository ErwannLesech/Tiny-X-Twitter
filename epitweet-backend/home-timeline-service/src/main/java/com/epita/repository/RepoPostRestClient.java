package com.epita.repository;

import com.epita.contracts.post.PostResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;

import java.util.List;
import java.util.logging.Logger;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;


@ApplicationScoped
public class RepoPostRestClient {
    private static final String POST_URL = "http://localhost:8082/api/posts/";
    private static Client client;
    private static final Logger logger = Logger.getLogger(RepoPostRestClient.class.getName());

    public RepoPostRestClient() {client = ClientBuilder.newClient();}

    public List<PostResponse> getPosts(String userId) {
        try {
            return client.target(POST_URL + "getPosts/" + userId)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<PostResponse>>() {});
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    public PostResponse getPost(String post_id) {
        try {
            return client.target(POST_URL + "getPost/" + post_id)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<PostResponse>() {
                    });
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }
}

