package com.epita.repository;

import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.*;

@ApplicationScoped
public class HomeTimelineRepository implements PanacheMongoRepository<HomeTimelineEntry> {

    public List<HomeTimelineEntry> getTimeline(final ObjectId userId) {
        return find("userId", userId).stream()
                .sorted(Comparator.comparing(HomeTimelineEntry::getDate))
                .toList();
    }

    public void addHomeEntry(HomeTimelineEntry entry) {
        this.persist(entry);
    }

    public void removeHomeEntry(ObjectId userId, ObjectId userFollowedId, ObjectId postId, EntryType type) {
        delete("userId = ?1 and userFollowedId = ?2 and postId = ?3 and type in ?4",
                userId, userFollowedId, postId, (type == null) ? List.of(EntryType.POST, EntryType.LIKE) : type);
    }
}
