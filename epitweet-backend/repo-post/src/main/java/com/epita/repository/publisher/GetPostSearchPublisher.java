package com.epita.repository.publisher;

import com.epita.payloads.search.GetPostSearchResponse;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending GetPostSearchResponse on getPostSearchResponse channel
 */
@ApplicationScoped
public class GetPostSearchPublisher {
    @Inject
    Logger logger;

    private final PubSubCommands<GetPostSearchResponse> publisher;

    public GetPostSearchPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(GetPostSearchResponse.class);
    }

    public void publish(final GetPostSearchResponse message) {
        logger.infof("Publishing FROM repo-post TO search-srvd ON " +
                "getPostSearchResponse FOR GetPostSearchResponse: %s", message.toString());
        publisher.publish("getPostSearchResponse", message);
    }
}
