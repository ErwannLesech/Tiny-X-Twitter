package com.epita.repository.publisher;

import com.epita.payloads.search.IndexPost;
import com.epita.payloads.sentiment.AnalysePost;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Publisher for sending IndexPost to the indexPost channel
 */
@ApplicationScoped
public class AnalysePostPublisher {
    @Inject
    Logger logger;

    private final PubSubCommands<AnalysePost> publisher;

    public AnalysePostPublisher(RedisDataSource ds) {
        this.publisher = ds.pubsub(AnalysePost.class);
    }

    public void publish(final AnalysePost message) {
        logger.infof("Publishing FROM repo-post TO sentiment-srvc ON " +
                "analysePost FOR AnalysePost: %s", message.toString());
        publisher.publish("analysePost", message);
    }
}
