package com.epita.repository;

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
        return this.find("userId", userId).stream().toList();
    }

    public void addHomeEntry(ObjectId userId, HomeTimelineEntry homeEntry) {
        List<HomeTimelineEntry> homeEntries = getTimeline(userId);
        if (homeEntries == null) {
            homeEntries = new ArrayList<>();
            homeEntries.add(homeEntry);
            this.persist(homeEntries);
        } else {
            homeEntries.add(homeEntry);
            this.update(homeEntries);
        }
    }

    public void removeHomeEntry(ObjectId userId, ObjectId userFollowedId) {
        List<HomeTimelineEntry> homeEntries = getTimeline(userId);
        if (homeEntries != null) {
            this.update(homeEntries.stream()
                    .filter(homeEntry -> !Objects.equals(homeEntry.getUserFollowedId(), userFollowedId))
                    .toList());
        }
    }

}
