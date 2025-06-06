package com.epita.repository.suscriber;

import com.epita.payloads.search.IndexPost;
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
public class IndexPostSubscriber implements Consumer<IndexPost> {

    @Inject
    SearchService searchService;

    private final PubSubCommands.RedisSubscriber subscriber;

    /**
     * Constructor to initialize Redis subscriber for indexing.
     *
     * @param ds the Redis data source.
     */
    public IndexPostSubscriber(final RedisDataSource ds) {
        subscriber = ds.pubsub(IndexPost.class)
                .subscribe("indexPost", this);
    }

    @Override
    public void accept(final IndexPost message) {
        vertx.executeBlocking(promise -> {
            try {
                if (message.getMethod().equals("creation")) {
                    searchService.indexPost(message);
                } else if (message.getMethod().equals("deletion")) {
                    searchService.deletePost(message.getPostId());
                }
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
