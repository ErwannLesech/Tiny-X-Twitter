package com.epita.repository.publisher;

import com.epita.payloads.userTimeline.PostTimeline;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending creation and deletion of posts to the "postTimeline" channel.
 */
@ApplicationScoped
public class PostTimelinePublisher {

    @Inject
    Logger logger;

    private final PubSubCommands<PostTimeline> publisher;

    public PostTimelinePublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(PostTimeline.class);
    }

    public void publish(final PostTimeline message) {
        logger.infof("Publishing FROM repo-post TO user-timeline ON " +
                "postTimeline FOR PostTimeline: %s", message.toString());
        publisher.publish("postTimeline", message);
    }
}
