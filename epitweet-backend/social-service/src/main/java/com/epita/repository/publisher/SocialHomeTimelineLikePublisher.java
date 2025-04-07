package com.epita.repository.publisher;

import com.epita.payloads.homeTimeline.SocialHomeTimelineLike;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending SocialHomeTimelineLike to the "socialHomeTimelineLike" channel.
 */
@ApplicationScoped
public class SocialHomeTimelineLikePublisher {

    @Inject
    Logger logger;

    private final PubSubCommands<SocialHomeTimelineLike> publisher;

    public SocialHomeTimelineLikePublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(SocialHomeTimelineLike.class);
    }

    public void publish(final SocialHomeTimelineLike message) {
        logger.infof("Publishing FROM social-service TO home-timeline-srvc ON " +
                "socialHomeTimelineLike FOR SocialHomeTimelineLike: %s", message.toString());
        publisher.publish("socialHomeTimelineLike", message);
    }
}