package com.epita.controller.subscriber;

import com.epita.payloads.social.BlockUser;
import com.epita.payloads.social.FollowUser;
import com.epita.service.HomeTimelineService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.function.Consumer;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

/**
 * A subscriber that listens to 'SocialHomeTimeline Follow'
 * channel related to unfollow/follow user response with repo social service module.
 */
@Startup
@ApplicationScoped
public class SocialFollowSubscriber implements Consumer<FollowUser> {

    @Inject
    Logger logger;

    @Inject
    HomeTimelineService homeTimelineService;

    private final PubSubCommands.RedisSubscriber subscriber;

    public SocialFollowSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(FollowUser.class)
                .subscribe("SocialHomeTimeline Follow", this);
    }

    @PostConstruct
    void init() {logger.info("SocialFollowSubscriber initiated !");}

    @Override
    public void accept(final FollowUser message) {
        // To keep things simple, we will avoid asynchronous stuff here,
        // so you need to tell Quarkus that you will execute blocking
        // code knowingly, otherwise it may crash at runtime to prevent
        // unwanted blocking code.
        vertx.executeBlocking(future -> {
            logger.infof("Received FollowUser from SocialHomeTimeline Follow: %s", message.toString());
            homeTimelineService.updateOnFollow(message);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
