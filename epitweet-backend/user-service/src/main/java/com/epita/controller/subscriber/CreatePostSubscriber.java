package com.epita.controller.subscriber;

import com.epita.payloads.post.CreatePostRequest;
import com.epita.service.UserService;
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

@Startup
@ApplicationScoped
public class CreatePostSubscriber implements Consumer<CreatePostRequest> {
    private PubSubCommands.RedisSubscriber subscriber;
    private final Vertx vertx;

    @Inject
    UserService userService;

    @Inject
    Logger logger;

    @Inject
    public CreatePostSubscriber(final RedisDataSource ds, Vertx vertx) {
        this.vertx = vertx;
        subscriber = ds.pubsub(CreatePostRequest.class).subscribe("isPostBlockedRequest", this);
    }

    @PostConstruct
    void init() {
        logger.info("CreatePostSubscriber initiated !");
    }

    @Override
    public void accept(final CreatePostRequest message) {
        logger.infof("Received CreatePostRequest result: %s", message.toString());
        vertx.executeBlocking(future -> {
            userService.createPostRequest(message);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
