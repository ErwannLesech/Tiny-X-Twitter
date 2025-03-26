package com.epita.repository.publisher;

import com.epita.payloads.post.CreatePostResponse;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreatePostPublisher {

    @Inject
    Logger logger;

    private PubSubCommands<CreatePostResponse> publisher;

    public CreatePostPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(CreatePostResponse.class);
    }

    public void publish(final CreatePostResponse message) {
        logger.infof("Publishing FROM user-service TO repo-post ON isPostBlockedResponse FOR createPostResponse: %s", message.toString());
        publisher.publish("isPostBlockedResponse", message);
    }
}
