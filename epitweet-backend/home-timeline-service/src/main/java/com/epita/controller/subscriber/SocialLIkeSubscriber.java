package com.epita.controller.subscriber;

import com.epita.payloads.homeTimeline.PostHomeTimeline;
import com.epita.payloads.social.LikePost;
import com.epita.service.HomeTimelineService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.function.Consumer;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

@Startup
@ApplicationScoped
public class SocialLIkeSubscriber  implements Consumer<LikePost> {

    @Inject
    Logger logger;

    @Inject
    HomeTimelineService homeTimelineService;

    private final PubSubCommands.RedisSubscriber subscriber;

    public SocialLIkeSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(LikePost.class)
                .subscribe("purchase-cmd", this);
    }

    @Override
    public void accept(final LikePost message) {
        // To keep things simple, we will avoid asynchronous stuff here,
        // so you need to tell Quarkus that you will execute blocking
        // code knowingly, otherwise it may crash at runtime to prevent
        // unwanted blocking code.
        vertx.executeBlocking(future -> {
            homeTimelineService.updateOnLike(message);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
