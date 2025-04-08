package com.epita;

import com.epita.repository.UserTimelineRepository;
import com.epita.repository.entity.UserTimelineEntry;
import com.epita.repository.entity.UserTimelineEntryAction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class UserTimelineRepositoryTest {

    @Inject
    UserTimelineRepository repository;

    ObjectId userId = new ObjectId();
    String postId = "post_123";
    Instant now = Instant.now();

    @BeforeEach
    public void setup() {
        List<UserTimelineEntry> existing = repository.findByUserId(userId);
        for (UserTimelineEntry entry : existing) {
            repository.delete(entry);
        }
    }

    @Test
    public void testCreateEntry_shouldPersist() {
        UserTimelineEntry entry = new UserTimelineEntry(userId, postId, UserTimelineEntryAction.LIKE, now);
        repository.createEntry(entry);

        List<UserTimelineEntry> result = repository.findByUserId(userId);
        assertEquals(1, result.size());
        assertEquals(postId, result.get(0).getPostId());
    }

    @Test
    public void testDeleteEntry_shouldRemoveMatchingEntry() {
        UserTimelineEntry entry = new UserTimelineEntry(userId, postId, UserTimelineEntryAction.LIKE, now);
        repository.createEntry(entry);

        assertEquals(1, repository.findByUserId(userId).size());

        repository.deleteEntry(entry);

        List<UserTimelineEntry> resultAfterDelete = repository.findByUserId(userId);
        System.out.println(resultAfterDelete);
        assertTrue(resultAfterDelete.isEmpty());
    }

    @Test
    public void testDeletePost_shouldRemoveAllEntriesWithGivenPostId() {
        ObjectId anotherUserId = new ObjectId();
        repository.createEntry(new UserTimelineEntry(anotherUserId, postId, UserTimelineEntryAction.LIKE, now));
        repository.createEntry(new UserTimelineEntry(userId, postId, UserTimelineEntryAction.CREATE, now.plusSeconds(5)));

        assertEquals(1, repository.findByUserId(userId).size());
        assertEquals(1, repository.findByUserId(anotherUserId).size());

        repository.deletePost(postId);

        assertTrue(repository.findByUserId(userId).isEmpty());
        assertTrue(repository.findByUserId(anotherUserId).isEmpty());
    }


    @Test
    public void testFindByUserId_shouldReturnEntriesInChronologicalOrder() {
        repository.createEntry(new UserTimelineEntry(userId, "post1", UserTimelineEntryAction.LIKE, now.plusSeconds(20)));
        repository.createEntry(new UserTimelineEntry(userId, "post2", UserTimelineEntryAction.LIKE, now));
        repository.createEntry(new UserTimelineEntry(userId, "post3", UserTimelineEntryAction.LIKE, now.plusSeconds(10)));

        List<UserTimelineEntry> result = repository.findByUserId(userId);
        result.sort(UserTimelineEntry::compareTo);

        assertEquals(3, result.size());
        assertEquals("post2", result.get(0).getPostId());
        assertEquals("post3", result.get(1).getPostId());
        assertEquals("post1", result.get(2).getPostId());
    }
}
