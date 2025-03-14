package com.epita.repository;

import com.epita.controller.contracts.PostRequest;
import com.epita.repository.entity.Post;
import com.epita.repository.entity.PostType;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class PostRepository implements PanacheMongoRepository<Post> {

    public Post findById(final ObjectId id) {
        return find("_id", id).firstResult();
    }

    public List<Post> findByUser(final ObjectId userId) {
        return find("userId", userId).list();
    }

    public void createPost(final Post post) {
        persist(post);
    }

    public void deletePost(final Post post) {
        delete(post);
    }

    public void clear() {
        deleteAll();
    }
}
