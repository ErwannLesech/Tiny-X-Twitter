package com.epita.controller.subscriber;

import com.epita.payloads.user.DeleteUserPost;
import com.epita.service.PostService;
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
 * A subscriber that listens to 'isPostBlockedResponse'
 * channel related to post-creation response with user-service module.
 */
@Startup
@ApplicationScoped
public class DeleteUserSubscriber implements Consumer<DeleteUserPost> {
    private final PubSubCommands.RedisSubscriber subscriber;
    private final Vertx vertx;

    @Inject
    PostService postService;

    @Inject
    Logger logger;

    @Inject
    public DeleteUserSubscriber(final RedisDataSource ds, Vertx vertx) {
        this.vertx = vertx;
        subscriber = ds.pubsub(DeleteUserPost.class).subscribe("deleteUserPost", this);
    }

    @PostConstruct
    void init() {
        logger.info("DeleteUserSubscriber initiated !");
    }

    @Override
    public void accept(final DeleteUserPost message) {
        logger.infof("Received CreationPostResponse from deleteUserPost: %s", message.toString());
        vertx.executeBlocking(future -> {
            postService.deletePost(message.getUserId());
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
