package com.epita.controller.subscriber;

import com.epita.payloads.search.GetPostSearchRequest;
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
 * A subscriber that listens to 'getPostSearchRequest'
 * channel related to post-search response with search service
 */
@Startup
@ApplicationScoped
public class GetPostSearchSubscriber implements Consumer<GetPostSearchRequest> {
    private final PubSubCommands.RedisSubscriber subscriber;
    private final Vertx vertx;

    @Inject
    PostService postService;

    @Inject
    Logger logger;

    @Inject
    public GetPostSearchSubscriber(final RedisDataSource ds, Vertx vertx) {
        this.vertx = vertx;
        subscriber = ds.pubsub(GetPostSearchRequest.class).subscribe("getPostSearchRequest", this);
    }

    @PostConstruct
    void init() {
        logger.info("GetPostSearchSubscriber initiated !");
    }

    @Override
    public void accept(final GetPostSearchRequest message) {
        logger.infof("Received CreationPostResponse from IsPostBlockedResponse: %s", message.toString());
        vertx.executeBlocking(future -> {
            postService.GetPostSearchResponse(message);
            future.complete();
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
