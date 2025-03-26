package com.epita;


import com.epita.controller.contracts.PostRequest;
import com.epita.service.SearchService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.function.Consumer;

import static io.quarkus.mongodb.runtime.dns.MongoDnsClientProvider.vertx;

@Startup
@ApplicationScoped
public class SearchDeleteSubscriber implements Consumer<PostRequest> {

    @Inject
    SearchService searchService;

    private final PubSubCommands.RedisSubscriber subscriber;

    /**
     * Constructor to initialize Redis subscriber for deletion.
     *
     * @param ds the Redis data source.
     */
    public SearchDeleteSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(PostRequest.class)
                .subscribe("search-delete-post", this);
    }

    @Override
    public void accept(final PostRequest postRequest) {
        vertx.executeBlocking(promise -> {
            try {
                searchService.deletePost(postRequest.getParentId());
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        });
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe();
    }
}
