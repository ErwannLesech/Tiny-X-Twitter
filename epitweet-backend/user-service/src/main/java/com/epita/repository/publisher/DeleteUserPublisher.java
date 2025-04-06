package com.epita.repository.publisher;

import com.epita.payloads.user.DeleteUserPost;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending IndexPost to the indexPost channel
 */
@ApplicationScoped
public class DeleteUserPublisher {
    @Inject
    Logger logger;

    private final PubSubCommands<DeleteUserPost> publisher;

    public DeleteUserPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(DeleteUserPost.class);
    }

    public void publish(final DeleteUserPost message) {
        logger.infof("Publishing FROM user-srvc TO repo-post ON " +
                "deleteUserPost FOR DeleteUserPost: %s", message.toString());
        publisher.publish("deleteUserPost", message);
    }
}
