package com.epita.repository;

import com.epita.controller.contracts.UserRequest;
import com.epita.repository.entity.User;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User> {

    public User findById(final ObjectId id) {
        return find("id", id).firstResult();
    }

    public User findByTag(final String tag) {
        return find("tag", tag).firstResult();
    }

    public void createUser(final User user) {
         persist(user);
    }

    public void updateUser(final User user) {
        update(user);
    }

    public void deleteUser(final User user) {
        delete(user);
    }
}
