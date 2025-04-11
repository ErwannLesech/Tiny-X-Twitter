package com.epita.controller.subscriber;

import com.epita.payloads.homeTimeline.PostHomeTimeline;
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
 * A subscriber that listens to 'postHomeTimeline'
 * channel related to post-creation response with repo post service module.
 */
@Startup
@ApplicationScoped
public class PostSubscriber implements Consumer<PostHomeTimeline> {

    @Inject
    Logger logger;

    @Inject
    HomeTimelineService homeTimelineService;

    private final PubSubCommands.RedisSubscriber subscriber;

    public PostSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(PostHomeTimeline.class)
                .subscribe("postHomeTimeline", this);
    }

    @PostConstruct
    void init() {logger.info("PostSubscriber initiated !");}


    @Override
    public void accept(final PostHomeTimeline message) {
        // To keep things simple, we will avoid asynchronous stuff here,
        // so you need to tell Quarkus that you will execute blocking
        // code knowingly, otherwise it may crash at runtime to prevent
        // unwanted blocking code.
        vertx.executeBlocking(future -> {
            logger.infof("Received PostHomeTimeline from postHomeTimeline: %s", message.toString());
            homeTimelineService.updateOnPost(message);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
