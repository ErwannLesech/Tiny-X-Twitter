package com.epita.repository;

import com.epita.repository.entity.User;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User> {

    @Inject
    Logger logger;

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user
     * @return the User object if found, or null
     */
    public User findById(final ObjectId id) {
        logger.infof("Fetching user by ID: %s", id);
        return find("_id", id).firstResult();
    }

    /**
     * Finds a user by their tag.
     *
     * @param tag the tag of the user
     * @return the User object if found, or null
     */
    public User findByTag(final String tag) {
        logger.infof("Fetching user by tag: %s", tag);
        return find("tag", tag).firstResult();
    }

    /**
     * Creates a new user in the database.
     *
     * @param user the User object to create
     */
    public void createUser(final User user) {
        logger.infof("Creating user: %s", user);
        persist(user);
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user the User object to update
     */
    public void updateUser(final User user) {
        logger.infof("Updating user: %s", user);
        update(user);
    }

    /**
     * Deletes a user from the database.
     *
     * @param user the User object to delete
     */
    public void deleteUser(final User user) {
        logger.infof("Deleting user: %s", user);
        delete(user);
    }

    /**
     * Clears all users from the database.
     */
    public void clear() {
        logger.info("Clearing all users from the database");
        deleteAll();
    }
}
