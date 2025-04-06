package com.epita.controller.subscriber;

import com.epita.payloads.post.CreatePostRequest;
import com.epita.service.SocialService;
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
 * A subscriber that listens to 'isPostBlockedRequest'
 * channel related to post-creation response with post-service module.
 */
@Startup
@ApplicationScoped
public class IsPostBlockedSubscriber implements Consumer<CreatePostRequest> {
    private final PubSubCommands.RedisSubscriber subscriber;
    private final Vertx vertx;

    @Inject
    Logger logger;

    @Inject
    SocialService socialService;

    @Inject
    public IsPostBlockedSubscriber(final RedisDataSource ds, Vertx vertx) {
        this.vertx = vertx;
        subscriber = ds.pubsub(CreatePostRequest.class).subscribe("isPostBlockedRequest", this);
    }

    @PostConstruct
    void init() {
        logger.info("IsPostBlockedSubscriber initiated !");
    }

    @Override
    public void accept(final CreatePostRequest message) {
        logger.infof("Received isPostBlockedRequest from CreatePostRequest: %s", message.toString());
        vertx.executeBlocking(future -> {
            // appel d'une méthode de SocialService qui va faire la logique (vérifier les block et set les booleens)
            // et qui va appeler le publisher ?
            socialService.checkPostBlocked(message);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}