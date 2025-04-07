package com.epita.repository;

import com.epita.payloads.social.BlockUser;
import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class HomeTimelineRepository implements PanacheMongoRepository<HomeTimelineEntry> {

    public static Map<ObjectId, List<ObjectId>> userBlockedList = new HashMap<>();

    public List<HomeTimelineEntry> getTimeline(final ObjectId userId) {
        return find("userId", userId).stream()
                .sorted(Comparator.comparing(HomeTimelineEntry::getDate))
                .toList();
    }

    public List<HomeTimelineEntry> getFollowers(ObjectId userId) {
        return this.findAll().stream()
                .filter(entry -> entry.getUserFollowedId().equals(userId))
                .toList();
    }

    public void addHomeEntry(HomeTimelineEntry entry) {
        this.persist(entry);
    }

    public void removeHomeEntry(ObjectId userId, ObjectId userFollowedId, ObjectId postId, EntryType type) {
        delete("userId = ?1 and userFollowedId = ?2 and post._id = ?3 and type in ?4",
                userId, userFollowedId, postId, (type == null) ? List.of(EntryType.POST, EntryType.LIKE) : type);
    }

    public void addBlockedUser(BlockUser blockUser) {
        if (!userBlockedList.containsKey(blockUser.userId())) {
            userBlockedList.put(blockUser.userId(), List.of(blockUser.userBlockedId()));
        } else {
            userBlockedList.get(blockUser.userId()).add(blockUser.userBlockedId());
        }
    }

    public void removeBlockedUser(BlockUser blockUser) {
        userBlockedList.get(blockUser.userId()).remove(blockUser.userBlockedId());
    }

    public boolean isNotBlocked(ObjectId userId, ObjectId userBlockedId) {
        return !userBlockedList.get(userId).contains(userBlockedId);
    }
}
