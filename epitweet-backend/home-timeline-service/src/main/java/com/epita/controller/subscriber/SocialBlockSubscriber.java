package com.epita.controller.subscriber;

import com.epita.payloads.homeTimeline.PostHomeTimeline;
import com.epita.payloads.social.BlockUser;
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
 * A subscriber that listens to 'SocialHomeTimeline Block'
 * channel related to unblock/block user response with repo social service module.
 */
@Startup
@ApplicationScoped
public class SocialBlockSubscriber implements Consumer<BlockUser> {

    @Inject
    Logger logger;

    @Inject
    HomeTimelineService homeTimelineService;

    private final PubSubCommands.RedisSubscriber subscriber;

    public SocialBlockSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(BlockUser.class)
                .subscribe("SocialHomeTimeline Block", this);
    }

    @PostConstruct
    void init() {logger.info("SocialBlockSubscriber initiated !");}

    @Override
    public void accept(final BlockUser message) {
        // To keep things simple, we will avoid asynchronous stuff here,
        // so you need to tell Quarkus that you will execute blocking
        // code knowingly, otherwise it may crash at runtime to prevent
        // unwanted blocking code.
        vertx.executeBlocking(future -> {
            logger.infof("Received BlockUser from SocialHomeTimeline Block: %s", message.toString());
            homeTimelineService.updateOnBlock(message);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}

