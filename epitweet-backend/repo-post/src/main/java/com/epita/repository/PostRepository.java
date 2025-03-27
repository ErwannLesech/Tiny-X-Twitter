package com.epita.repository;

import com.epita.repository.entity.Post;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Repository for managing Post entities in MongoDB.
 */
@ApplicationScoped
public class PostRepository implements PanacheMongoRepository<Post> {

    @Inject
    Logger logger;

    /**
     * Finds a Post by its ID.
     *
     * @param id The ID of the Post to find.
     * @return The Post with the specified ID, or null if not found.
     */
    public Post findById(final ObjectId id) {
        logger.infof("Finding Post by ID: %s", id);
        return find("_id", id).firstResult();
    }

    /**
     * Finds all Posts by the user ID.
     *
     * @param userId The ID of the user whose posts to find.
     * @return A list of Posts created by the specified user.
     */
    public List<Post> findByUser(final ObjectId userId) {
        logger.infof("Finding Posts by user ID: %s", userId);
        return find("userId", userId).list();
    }

    /**
     * Creates a new Post in the repository.
     *
     * @param post The Post to create.
     */
    public void createPost(final Post post) {
        logger.infof("Creating new Post: %s", post);
        persist(post);
    }

    /**
     * Deletes a Post from the repository.
     *
     * @param post The Post to delete.
     */
    public void deletePost(final Post post) {
        logger.infof("Deleting Post: %s", post);
        delete(post);
    }

    /**
     * Deletes all Posts from the repository.
     */
    public void clear() {
        logger.info("Clearing all Posts from the repository");
        deleteAll();
    }
}
