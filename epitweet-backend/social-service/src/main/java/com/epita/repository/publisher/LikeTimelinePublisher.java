package com.epita.repository.publisher;

import com.epita.payloads.userTimeline.LikeTimeline;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending LikeTimeline to the "likeTimeline" channel.
 */
@ApplicationScoped
public class LikeTimelinePublisher {

    @Inject
    Logger logger;

    private final PubSubCommands<LikeTimeline> publisher;

    public LikeTimelinePublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(LikeTimeline.class);
    }

    public void publish(final LikeTimeline message) {
        logger.infof("Publishing FROM social-service TO user-timeline-srvc ON " +
                "likeTimeline FOR LikeTimeline: %s", message.toString());
        publisher.publish("likeTimeline", message);
    }
}