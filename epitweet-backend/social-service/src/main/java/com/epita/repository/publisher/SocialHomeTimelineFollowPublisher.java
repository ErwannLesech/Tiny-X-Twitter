package com.epita.repository.publisher;

import com.epita.payloads.homeTimeline.SocialHomeTimelineFollow;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending Social Home Timeline Follow to the "socialHomeTimelineFollow" channel.
 */
@ApplicationScoped
public class SocialHomeTimelineFollowPublisher {

    @Inject
    Logger logger;

    private final PubSubCommands<SocialHomeTimelineFollow> publisher;

    public SocialHomeTimelineFollowPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(SocialHomeTimelineFollow.class);
    }

    public void publish(final SocialHomeTimelineFollow message) {
        logger.infof("Publishing FROM social-service TO home-timeline-srvc ON " +
                "socialHomeTimelineFollow FOR SocialHomeTimelineFollow: %s", message.toString());
        publisher.publish("socialHomeTimelineFollow", message);
    }
}