package com.epita.repository.publisher;

import com.epita.payloads.post.CreatePostResponse;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending IsPostBlockedRequest to the "isPostBlockedRequest" channel.
 */
@ApplicationScoped
public class IsPostBlockedPublisher {

    @Inject
    Logger logger;

    private final PubSubCommands<CreatePostResponse> publisher;

    public IsPostBlockedPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(CreatePostResponse.class);
    }

    public void publish(final CreatePostResponse message) {
        logger.infof("Publishing FROM social-service TO repo-post ON " +
                "isPostBlockedResponse FOR CreatePostResponse: %s", message.toString());
        publisher.publish("isPostBlockedResponse", message);
    }
}