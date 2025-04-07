package com.epita.service;

import com.epita.controller.contracts.UserTimelinePost;
import com.epita.controller.contracts.UserTimelineResponse;
import com.epita.converter.UserTimelineConverter;
import com.epita.payloads.userTimeline.LikeTimeline;
import com.epita.payloads.userTimeline.PostTimeline;
import com.epita.repository.UserTimelineRepository;
import com.epita.repository.entity.UserTimelineEntry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Service responsible for handling user timeline operations,
 * including processing post and like events and retrieving user timeline.
 */
@ApplicationScoped
public class UserTimelineService {
    /**
     * Logger used for recording informational, warning, or error messages
     * within the {@code UserTimelineService}.
     */
    @Inject
    Logger logger;
    /**
     * Repository for interacting with the MongoDB collection that stores
     * user timeline entities. Provides methods to create, delete, and retrieve entries.
     */
    @Inject
    UserTimelineRepository userTimelineRepository;

    /**
     * Retrieves the timeline of a specific user, ordered by performed date.
     *
     * @param userId The ID of the user whose timeline is to be retrieved.
     * @return A list of {@link UserTimelinePost} representing the user's timeline entries.
     */
    public UserTimelineResponse getUserTimeline(ObjectId userId) {
        logger.infof("Fetching timeline for user: %s", userId);
        List<UserTimelineEntry> userTimeline = userTimelineRepository.findByUserId(userId);
        List<UserTimelinePost> userTimelinePosts = userTimeline
                .stream()
                .sorted(UserTimelineEntry::compareTo)
                .map(UserTimelineConverter::toResponse)
                .toList();
        return new UserTimelineResponse(userId, userTimelinePosts);
    }

    /**
     * Processes a post creation or deletion event and updates the timeline accordingly.
     *
     * @param postTimeline The {@link PostTimeline} payload representing the post event.
     */
    public void handlePostTimeline(PostTimeline postTimeline) {
        logger.infof("Processing post timeline event with method: %s", postTimeline.getMethod());
        UserTimelineEntry userTimelineEntry = UserTimelineConverter.toEntity(postTimeline);
        if (postTimeline.getMethod().equalsIgnoreCase("creation")) {
            userTimelineRepository.createEntry(userTimelineEntry);
        } else if (postTimeline.getMethod().equalsIgnoreCase("deletion")) {
            userTimelineRepository.deleteEntry(userTimelineEntry);
        } else {
            logger.warnf(
                    "PostTimeline.method should be \\\"creation\\\" or \\\"deletion\\\", not \" %s",
                    postTimeline.getMethod()
            );
        }
    }

    /**
     * Processes a like or unlike event and updates the timeline accordingly.
     *
     * @param likeTimeline The {@link LikeTimeline} payload representing the like event.
     */
    public void handleLikeTimeline(LikeTimeline likeTimeline) {
        logger.infof("Processing like timeline event with method: %s", likeTimeline.getMethod());
        UserTimelineEntry userTimelineEntry = UserTimelineConverter.toEntity(likeTimeline);
        if (likeTimeline.getMethod().equalsIgnoreCase("like")) {
            userTimelineRepository.createEntry(userTimelineEntry);
        } else if (likeTimeline.getMethod().equalsIgnoreCase("unlike")) {
            userTimelineRepository.deleteEntry(userTimelineEntry);
        } else {
            logger.warnf(
                    "LikeTimeline.method should be \\\"like\\\" or \\\"unlike\\\", not \" %s",
                    likeTimeline.getMethod()
            );
        }
    }
}
