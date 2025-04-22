package com.epita.repository;

import com.epita.repository.entity.Sentiment;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SentimentRepository implements PanacheMongoRepository<Sentiment> {

    @Inject
    Logger logger;

    /**
     * Finds a sentiment by its postId
     */
    public Sentiment findByPostId(final String postId) {
        ObjectId id = new ObjectId(postId);
        logger.infof("Finding Sentiment by postId: %s", id);
        return find("postId", id).firstResult();
    }

    /**
     * Creates a sentiment in db
     */
    public void createSentiment(final Sentiment sentiment) {
        logger.infof("Creating Sentiment %s", sentiment);
        persist(sentiment);
    }

    /**
     * Deletes a sentiment of db
     */
    public void deleteSentiment(final String postId) {
        Sentiment sentiment = findByPostId(postId);
        if (sentiment != null) {
            logger.infof("Deleting Sentiment %s", sentiment.toString());
            delete(sentiment);
        }
        else {
            logger.infof("Sentiment to delete %s not found", postId);
        }
    }
}
