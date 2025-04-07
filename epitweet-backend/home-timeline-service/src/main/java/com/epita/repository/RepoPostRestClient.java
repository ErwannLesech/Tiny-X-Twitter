package com.epita.repository;

import com.epita.contracts.post.PostResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import java.util.logging.Logger;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;


@ApplicationScoped
public class RepoPostRestClient {
    private static final String POST_URL = "http://localhost:8082/api/posts/getPosts";
    private static Client client;
    private static final Logger logger = Logger.getLogger(RepoPostRestClient.class.getName());

    public RepoPostRestClient() {client = ClientBuilder.newClient();}

    public PostResponse getPosts() {
        try {
            return client.target(POST_URL)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<PostResponse>() {});
        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }
}

