package com.epita.repository.publisher;

import com.epita.payloads.search.IndexPost;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending IndexPost to the indexPost channel
 */
@ApplicationScoped
public class IndexPostPublisher {
    @Inject
    Logger logger;

    private final PubSubCommands<IndexPost> publisher;

    public IndexPostPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(IndexPost.class);
    }

    public void publish(final IndexPost message) {
        logger.infof("Publishing FROM repo-post TO search-srvc ON " +
                "indexPost FOR indexPost: %s", message.toString());
        publisher.publish("indexPost", message);
    }
}
