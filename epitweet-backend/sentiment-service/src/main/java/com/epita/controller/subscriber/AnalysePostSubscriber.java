package com.epita.controller.subscriber;

import com.epita.payloads.sentiment.AnalysePost;
import com.epita.service.SentimentService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.function.Consumer;
/**
 * A subscriber that listens to 'analysePost'.
 */
@Startup
@ApplicationScoped
public class AnalysePostSubscriber implements Consumer<AnalysePost> {
    private final PubSubCommands.RedisSubscriber subscriber;
    private final Vertx vertx;

    @Inject
    SentimentService sentimentService;

    @Inject
    Logger logger;

    @Inject
    public AnalysePostSubscriber(final RedisDataSource ds, Vertx vertx) {
        this.vertx = vertx;
        subscriber = ds.pubsub(AnalysePost.class).subscribe("analysePost", this);
    }

    @PostConstruct
    void init() {
        logger.info("AnalysePostSubscriber initiated !");
    }

    @Override
    public void accept(final AnalysePost message) {
        logger.infof("Received AnalysePost from analysePost: %s", message.toString());
        vertx.executeBlocking(future -> {
            sentimentService.handlePost(message);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}