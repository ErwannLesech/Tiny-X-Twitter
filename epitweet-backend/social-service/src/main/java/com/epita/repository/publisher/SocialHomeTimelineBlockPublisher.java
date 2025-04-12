package com.epita.repository.publisher;

import com.epita.payloads.homeTimeline.SocialHomeTimelineBlock;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending Social Home Timeline Block to the "socialHomeTimelineBlock" channel.
 */
@ApplicationScoped
public class SocialHomeTimelineBlockPublisher {

    @Inject
    Logger logger;

    private final PubSubCommands<SocialHomeTimelineBlock> publisher;

    public SocialHomeTimelineBlockPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(SocialHomeTimelineBlock.class);
    }

    public void publish(final SocialHomeTimelineBlock message) {
        logger.infof("Publishing FROM social-service TO home-timeline-srvc ON " +
                "socialHomeTimelineBlock FOR SocialHomeTimelineBlock: %s", message.toString());
        publisher.publish("socialHomeTimelineBlock", message);
    }
}