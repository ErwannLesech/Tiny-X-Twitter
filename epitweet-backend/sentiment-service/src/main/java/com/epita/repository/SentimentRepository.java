package com.epita.repository;

import com.epita.repository.entity.Sentiment;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SentimentRepository implements PanacheMongoRepository<Sentiment> {

    @Inject
    Logger logger;

    /**
     * Finds a sentiment by its postId
     */
    public Sentiment findByPostId(final String postId) {
        logger.infof("Finding Sentiment by postId: %s", postId);
        return find("postId", postId).firstResult();
    }

    /**
     * Creates a sentiment in db
     */
    public void createSentiment(final Sentiment sentiment) {
        persist(sentiment);
    }

    /**
     * Deletes a sentiment of db
     */
    public void deleteSentiment(final String postId) {
        Sentiment sentiment = findByPostId(postId);
        delete(sentiment);
    }
}
