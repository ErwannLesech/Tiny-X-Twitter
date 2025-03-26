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
public class SearchIndexSubscriber implements Consumer<PostRequest> {

    @Inject
    SearchService searchService;

    private final PubSubCommands.RedisSubscriber subscriber;

    /**
     * Constructor to initialize Redis subscriber for indexing.
     *
     * @param ds the Redis data source.
     */
    public SearchIndexSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(PostRequest.class)
                .subscribe("search-index-post", this);
    }

    @Override
    public void accept(final PostRequest message) {
        vertx.executeBlocking(promise -> {
            try {
                searchService.indexPost(message);
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
