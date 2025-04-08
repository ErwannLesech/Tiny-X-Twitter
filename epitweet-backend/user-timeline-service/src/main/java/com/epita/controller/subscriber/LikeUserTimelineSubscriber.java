package com.epita.controller.subscriber;

import com.epita.payloads.userTimeline.LikeTimeline;
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
 * A subscriber that listens to the 'likeTimeline' channel
 * related to a user liking or disliking a post with repo-social module.
 */
@Startup
@ApplicationScoped
public class LikeUserTimelineSubscriber implements Consumer<LikeTimeline> {
    /**
     * Logger for subscriber events and lifecycle actions.
     */
    @Inject
    Logger logger;

    /**
     * Service responsible for processing like/unlike timeline updates.
     */
    @Inject
    UserTimelineService userTimelineService;

    /**
     * Redis subscriber for messages published to the 'likeTimeline' channel.
     */
    private final PubSubCommands.RedisSubscriber subscriber;

    /**
     * Vert.x instance used to run blocking operations asynchronously.
     */
    private final Vertx vertx;

    public LikeUserTimelineSubscriber(final RedisDataSource ds, Vertx vertx) {
        subscriber = ds.pubsub(LikeTimeline.class).subscribe("likeTimeline", this);
        this.vertx = vertx;
    }

    @PostConstruct
    void init() {
        logger.info("LikeUserTimelineSubscriber initiated !");
    }

    @Override
    public void accept(LikeTimeline likeTimeline) {
        logger.infof("Received LikeTimeline from likeTimeline: %s", likeTimeline.toString());
        vertx.executeBlocking(future -> {
            userTimelineService.handleLikeTimeline(likeTimeline);
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