package com.epita.repository;

import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class HomeTimelineRepository implements PanacheMongoRepository<HomeTimelineEntry> {

    @Inject
    Logger logger;

    /**
     * Get a {@code List<HomeTimelineEntry>} of a {@code ObjectId} user id.
     *
     * @param userId The {@code ObjectId} of the user who home timeline is to be to retrieve.
     * @return The {@code List<HomeTimelineEntry>} representing the home timeline.
     */
    public List<HomeTimelineEntry> getTimeline(final ObjectId userId) {
        return find("userId", userId).stream()
                .sorted(Comparator.comparing(HomeTimelineEntry::getDate))
                .toList();
    }

    public List<HomeTimelineEntry> getRandomTimeline(final ObjectId userId) {
        List<HomeTimelineEntry> entries =  find("userId != ?1", userId).stream()
                .sorted(Comparator.comparing(HomeTimelineEntry::getDate))
                .toList();

        Map<ObjectId, HomeTimelineEntry> uniqueEntriesMap = entries.stream()
                .collect(Collectors.toMap(
                        HomeTimelineEntry::getUserId,
                        user -> user,
                        (existing, replacement) -> existing
                ));

        return new ArrayList<>(uniqueEntriesMap.values());
    }

    /**
     * Add a {@code HomeTimelineEntry} to the database.
     *
     * @param entry The {@code HomeTimelineEntry} to add.
     */
    public void addHomeEntry(HomeTimelineEntry entry) {
        // Check for already existing same entry
        List<HomeTimelineEntry> entries = getTimeline(entry.getUserId());

        for (HomeTimelineEntry e : entries) {
            // avoid adding same post to timeline
            if (e.getPostId().equals(entry.getPostId())) {
                return;
            }
        }

        logger.infof("Adding entry: %s", entry.toString());

        this.persist(entry);
    }

    /**
     * Remove a {@code HomeTimelineEntry} from the database.
     *
     * @param entry The {@code HomeTimelineEntry} to add.
     * @param type The {@code EntryType} of the {@code HomeTimelineEntry} to add.
     */
    public void removeHomeEntry(HomeTimelineEntry entry, EntryType type) {

        logger.infof("Removing entry: %s", entry.toString());

        delete("userId = ?1 and userFollowedId = ?2 and postId = ?3 and postType = ?4",
                entry.getUserId(), entry.getUserFollowedId(), entry.getPostId(), type);
    }

    public void removeHomeEntryWithoutLink(HomeTimelineEntry entry, EntryType type) {

        logger.infof("Removing entry: %s", entry.toString());

        delete("userId = ?1 and postId = ?2 and postType = ?3",
                entry.getUserId(), entry.getPostId(), type);
    }

    /**
     * Remove every {@code HomeTimelineEntry} relate to {@code ObjectId} user from the timeline of {@code ObjectId} user
     *
     * @param userId The {@code ObjectId} user.
     * @param userFollowed The {@code ObjectIf} user to remove.
     */
    public void removeUserFromTimeline(final ObjectId userId, final ObjectId userFollowed) {

        logger.infof("Removing user: %s", userId.toString());

        delete("userId = ?1 and userFollowedId = ?2", userId, userFollowed);
    }

    /**
     * Remove a {@code HomeTimelineEntry} relate to {@code ObjectId} post from the timeline of {@code ObjectId} user
     *
     * @param userId The {@code ObjectId} user.
     * @param postId The {@code ObjectIf} user to remove.
     */
    public void removePostFromTimeline(final ObjectId userId, final ObjectId postId) {
        logger.infof("Removing post: %s", postId.toString());
        delete("userId = ?1 and postId= ?2", userId, postId);
    }
}
