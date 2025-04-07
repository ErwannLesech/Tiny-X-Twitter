package com.epita.repository;

import com.epita.contracts.post.PostResponse;
import com.epita.repository.entity.HomeTimelineEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class HomeTimelineRepository implements PanacheMongoRepository<HomeTimelineEntry> {

    public List<HomeTimelineEntry> getTimeline(final ObjectId userId) {
        List<HomeTimelineEntry> entries = this.findAll().stream().toList();
        return entries.stream()
                .filter(entry -> Objects.equals(entry.getUserId(), userId))
                .toList();
    }

    public List<ObjectId> getFollowers(ObjectId userId) {
        List<HomeTimelineEntry> entries = this.findAll().list();
        return entries.stream()
                .filter(entry -> entry.getUserFollowedId().equals(userId))
                .map(HomeTimelineEntry::getUserId)
                .toList();
    }

    public void addHomeEntry(HomeTimelineEntry entry) {
        this.persist(entry);
    }

    public void removeHomeEntry(ObjectId userId, ObjectId followedId, ObjectId postId) {

    }
}
