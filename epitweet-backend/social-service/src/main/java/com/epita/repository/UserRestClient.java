package com.epita.repository;

import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.util.logging.Logger;

/**
 * REST client for interacting with the User Service.
 */
@ApplicationScoped
public class UserRestClient {

    private static final String USER_URL = "http://localhost:8083/api/users";
    private static final Logger LOGGER = Logger.getLogger(UserRestClient.class.getName());
    private final Client client;

    public UserRestClient() {
        this.client = ClientBuilder.newClient();
    }

    public Response getUser(String userId) {
        try {
            return client.target(USER_URL + "/" + userId)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<Response>() {});
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            return null;
        }
    }
}