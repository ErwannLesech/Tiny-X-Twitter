package com.epita.repository.publisher;

import com.epita.repository.publisher.contracts.CreatePostRequest;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreatePostPublisher {

    @Inject
    Logger logger;

    private final PubSubCommands<CreatePostRequest> publisher;

    public CreatePostPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(CreatePostRequest.class);
    }

    public void publish(final CreatePostRequest message) {
        logger.infof("Publishing FROM repo-post TO user-service ON isPostBlockedRequest FOR createPostRequest: %s", message.toString());
        publisher.publish("isPostBlockedRequest", message);
    }
}
