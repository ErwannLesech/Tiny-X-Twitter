package com.epita.controller.subscriber;

import com.epita.payloads.userTimeline.PostTimeline;
import com.epita.service.UserTimelineService;
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
 * A subscriber that listens to the 'postTimeline' channel
 * related to a user creating or deleting a post with repo-post module.
 */
@Startup
@ApplicationScoped
public class PostUserTimelineSubscriber implements Consumer<PostTimeline> {
    /**
     * Logger for logging events related to this subscriber.
     */
    @Inject
    Logger logger;

    /**
     * Service handling user timeline updates.
     */
    @Inject
    UserTimelineService userTimelineService;

    /**
     * Redis subscriber for the 'postTimeline' channel.
     */
    private final PubSubCommands.RedisSubscriber subscriber;

    /**
     * Vert.x instance used to offload processing to worker threads.
     */
    private final Vertx vertx;

    public PostUserTimelineSubscriber(final RedisDataSource ds, Vertx vertx) {
        subscriber = ds.pubsub(PostTimeline.class).subscribe("postTimeline", this);
        this.vertx = vertx;
    }

    @PostConstruct
    void init() {
        logger.info("PostUserTimelineSubscriber initiated !");
    }

    @Override
    public void accept(PostTimeline postTimeline) {
        logger.infof("Received PostTimeline from postTimeline: %s", postTimeline.toString());
        vertx.executeBlocking(future -> {
            userTimelineService.handlePostTimeline(postTimeline);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        try {
            logger.info("Unsubscribing from 'likeTimeline' channel...");
            subscriber.unsubscribe();
        } catch (IllegalStateException e) {
            logger.warn("Redis connection already closed during shutdown. Skipping unsubscribe.");
        } catch (Exception e) {
            logger.error("Unexpected error while unsubscribing from Redis channel.", e);
        }
    }
}
