package com.epita.repository;

import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.*;

@ApplicationScoped
public class HomeTimelineRepository implements PanacheMongoRepository<HomeTimelineEntry> {

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

    /**
     * Add a {@code HomeTimelineEntry} to the database.
     *
     * @param entry The {@code HomeTimelineEntry} to add.
     */
    public void addHomeEntry(HomeTimelineEntry entry) {
        this.persist(entry);
    }

    /**
     * Remove a {@code HomeTimelineEntry} from the database.
     *
     * @param entry The {@code HomeTimelineEntry} to add.
     * @param type The {@code EntryType} of the {@code HomeTimelineEntry} to add.
     */
    public void removeHomeEntry(HomeTimelineEntry entry, EntryType type) {
        delete("userId = ?1 and userFollowedId = ?2 and postId = ?3 and postType = ?4",
                entry.getUserId(), entry.getUserFollowedId(), entry.getPostId(), type);
    }

    /**
     * Remove every {@code HomeTimelineEntry} relate to {@code ObjectId} user from the timeline of {@code ObjectId} user
     *
     * @param userId The {@code ObjectId} user.
     * @param userFollowed The {@code ObjectIf} user to remove.
     */
    public void removeUserFromTimeline(final ObjectId userId, final ObjectId userFollowed) {
        delete("userId = ?1 and userFollowedId = ?2", userId, userFollowed);
    }
}
