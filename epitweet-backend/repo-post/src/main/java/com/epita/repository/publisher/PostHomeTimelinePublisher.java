package com.epita.repository.publisher;

import com.epita.payloads.homeTimeline.PostHomeTimeline;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending creation and deletion of posts to the "postHomeTimeline" channel.
 */
@ApplicationScoped
public class PostHomeTimelinePublisher {

    @Inject
    Logger logger;

    private final PubSubCommands<PostHomeTimeline> publisher;

    public PostHomeTimelinePublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(PostHomeTimeline.class);
    }

    public void publish(final PostHomeTimeline message) {
        logger.infof("Publishing FROM repo-post TO home-timeline ON " +
                "postHomeTimeline FOR PostHomeTimeline: %s", message.toString());
        publisher.publish("postHomeTimeline", message);
    }
}
