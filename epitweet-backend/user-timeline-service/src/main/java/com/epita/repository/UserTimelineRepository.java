package com.epita.repository;

import com.epita.repository.entity.UserTimelineEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Repository for managing {@link UserTimelineEntry} entities in the MongoDB.
 */
@ApplicationScoped
public class UserTimelineRepository implements PanacheMongoRepository<UserTimelineEntry> {
    /**
     * Logger used for recording informational, warning, or error messages
     * within the {@code UserTimelineRepository}.
     */
    @Inject
    Logger logger;

    /**
     * Retrieves all timeline entries associated with the specified user ID.
     *
     * @param userId The {@link ObjectId} of the user whose timeline entries are to be fetched.
     * @return A list of {@link UserTimelineEntry} documents associated with the given user ID.
     */
    public List<UserTimelineEntry> findByUserId(ObjectId userId) {
        logger.infof("Finding entries with user ID: %s", userId.toString());
        return find("userId", userId).list();
    }

    /**
     * Create a new user timeline entry in the MongoDB collection.
     *
     * @param userTimelineEntry The {@link UserTimelineEntry} to be created.
     */
    public void createEntry(UserTimelineEntry userTimelineEntry) {
        logger.infof("Creating user timeline entry: %s", userTimelineEntry);
        persist(userTimelineEntry);
    }

    /**
     * Deletes an existing user timeline entry based on {@code userId}, {@code postId},
     * and the type of action (like or create).
     *
     * @param userTimelineEntry The {@link UserTimelineEntry} to be deleted. The entry is matched
     *                          by its {@code userId}, {@code postId}, and {@code userTimelineEntryAction}.
     */
    public void deleteEntry(UserTimelineEntry userTimelineEntry) {
        logger.infof(
                "Deleting user timeline entry with same userId, postId end userTimelineEntryAction than %s",
                userTimelineEntry
        );
        delete(
                "userId = ?1 and postId = ?2 and userTimelineEntryAction = ?3",
                userTimelineEntry.getUserId(),
                userTimelineEntry.getPostId(),
                userTimelineEntry.getUserTimelineEntryAction());
    }

    /**
     * Deletes all timeline entries associated with the given post ID.
     *
     * @param postId The ID of the post to remove from user timelines.
     */
    public void deletePost(String postId) {
        logger.infof("Deleting all user timeline entries related to postId: %s", postId);
        delete("postId", postId);
    }
}
