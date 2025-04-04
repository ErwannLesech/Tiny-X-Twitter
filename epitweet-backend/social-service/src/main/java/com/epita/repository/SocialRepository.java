package com.epita.repository;

import com.epita.controller.contracts.AppreciationRequest;
import com.epita.controller.contracts.BlockUnblockRequest;
import com.epita.controller.contracts.FollowUnfollowRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class SocialRepository {
    private static final Logger LOG = Logger.getLogger(SocialRepository.class);
    @Inject
    Driver neo4jDriver;

    /**
     * Creates or updates the follow relation between two users.
     * @param request the request indicating who follows or unfollows whom
     */
    public void followUnfollow(FollowUnfollowRequest request) {
        try (var session = neo4jDriver.session()) {
            if (request.isFollowUnfollow()) {
                session.executeWrite(tx -> {
                    tx.run(
                            "MATCH (u1:User {userId: $userFollowId}), (u2:User {userId: $userFollowedId}) " +
                                    "MERGE (u1)-[:FOLLOWS]->(u2)",
                            Map.of(
                                    "userFollowId", request.getUserFollowId(),
                                    "userFollowedId", request.getUserFollowedId()
                            )
                    );
                    return null;
                });
                LOG.infof("User %s followed user %s", request.getUserFollowId(), request.getUserFollowedId());
            } else {
                session.executeWrite(tx -> {
                    tx.run(
                            "MATCH (u1:User {userId: $userFollowId})-[r:FOLLOWS]->(u2:User {userId: $userFollowedId}) " +
                                    "DELETE r",
                            Map.of(
                                    "userFollowId", request.getUserFollowId(),
                                    "userFollowedId", request.getUserFollowedId()
                            )
                    );
                    return null;
                });
                LOG.infof("User %s unfollowed user %s", request.getUserFollowId(), request.getUserFollowedId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process follow/unfollow", e);
        }
    }

    /**
     * Gets the users followed by a specific user.
     * @param userId the user for whom to get the followed users
     * @return a list of userIds who are followed by the specified userId
     */
    public List<String> getFollows(String userId) {
        try (var session = neo4jDriver.session()) {
            String cypher = "MATCH (u:User {userId: $userId})-[r:FOLLOWS]->(followed:User) " +
                            "RETURN followed.userId";
            var result = session.executeRead(tx ->
                    tx.run(cypher, Map.of("userId", userId)).list()
            );
            List<String> follows = new ArrayList<>();
            for (Record record : result) {
                follows.add(record.get("followed.userId").asString());
            }
            return follows;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve follows", e);
        }
    }

    /**
     * Gets the followers of a specific user.
     * @param userId the user for whom to get the followers
     * @return a list of userIds who follow the specified userId
     */
    public List<String> getFollowers(String userId) {
        try (var session = neo4jDriver.session()) {
            String cypher = "MATCH (u:User)-[:FOLLOWS]->(target:User {userId: $userId}) " +
                            "RETURN u.userId";
            var result = session.executeRead(tx ->
                    tx.run(cypher, Map.of("userId", userId)).list()
            );
            List<String> followers = new ArrayList<>();
            for (Record record : result) {
                followers.add(record.get("u.userId").asString());
            }
            return followers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve followers", e);
        }
    }

    /**
     * Creates or updates the block relation between two users.
     * @param request the request indicating who blocks or unblocks whom
     */
    public void blockUnblock(BlockUnblockRequest request) {
        try (var session = neo4jDriver.session()) {
            if (request.isBlockUnblock()) {
                session.executeWrite(tx -> {
                    tx.run(
                            "MATCH (u1:User {userId: $userBlockId}), (u2:User {userId: $userBlockedId}) " +
                                    "MERGE (u1)-[:BLOCKS]->(u2)",
                            Map.of(
                                    "userBlockId", request.getUserBlockId(),
                                    "userBlockedId", request.getUserBlockedId()
                            )
                    );
                    return null;
                });
                LOG.infof("User %s blocked user %s", request.getUserBlockId(), request.getUserBlockedId());
            } else {
                session.executeWrite(tx -> {
                    tx.run(
                            "MATCH (u1:User {userId: $userBlockId})-[r:BLOCKS]->(u2:User {userId: $userBlockedId}) " +
                                    "DELETE r",
                            Map.of(
                                    "userBlockId", request.getUserBlockId(),
                                    "userBlockedId", request.getUserBlockedId()
                            )
                    );
                    return null;
                });
                LOG.infof("User %s unblocked user %s", request.getUserBlockId(), request.getUserBlockedId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process block/unblock", e);
        }
    }

    /**
     * Gets the users blocked by a specific user.
     * @param userId the user for whom to get the blocked users
     * @return a list of userIds who are blocked by the specified userId
     */
    public List<String> getBlockedUsers(String userId) {
        try (var session = neo4jDriver.session()) {
            String cypher = "MATCH (u:User {userId: $userId})-[r:BLOCKS]->(blocked:User) " +
                            "RETURN blocked.userId";
            var result = session.executeRead(tx ->
                    tx.run(cypher, Map.of("userId", userId)).list()
            );
            List<String> blockedUsers = new ArrayList<>();
            for (Record record : result) {
                blockedUsers.add(record.get("blocked.userId").asString());
            }
            return blockedUsers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve blocked users", e);
        }
    }

    /**
     * Gets the users who blocked a specific user.
     * @param userId the user for whom to get the users who blocked them
     * @return a list of userIds who have blocked the specified userId
     */
    public List<String> getUsersWhoBlocked(String userId) {
        try (var session = neo4jDriver.session()) {
            String cypher = "MATCH (u:User)-[r:BLOCKS]->(target:User {userId: $userId}) " +
                            "RETURN u.userId";
            var result = session.executeRead(tx ->
                    tx.run(cypher, Map.of("userId", userId)).list()
            );
            List<String> usersWhoBlocked = new ArrayList<>();
            for (Record record : result) {
                usersWhoBlocked.add(record.get("u.userId").asString());
            }
            return usersWhoBlocked;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve users who blocked", e);
        }
    }

    /**
     * Creates or updates the like relation between one user and one post.
     * @param request the request indicating who like or unlike which post
     */
    public void likeUnlike(AppreciationRequest request) {
        try (var session = neo4jDriver.session()) {
            if (request.isLikeUnlike()) {
                session.executeWrite(tx -> {
                    tx.run(
                            "MATCH (u:User {userId: $userId}), (p:Post {postId: $postId}) " +
                                    "MERGE (u)-[r:LIKES {dateTime: $dateTime}]->(p)",
                            Map.of(
                                    "userId", request.getUserId(),
                                    "postId", request.getPostId(),
                                    "dateTime", LocalDateTime.now().toString()
                            )
                    );
                    return null;
                });
                LOG.infof("User %s liked post %s", request.getUserId(), request.getPostId());
            } else {
                session.executeWrite(tx -> {
                    tx.run(
                            "MATCH (u:User {userId: $userId})-[r:LIKES]->(p:Post {postId: $postId}) " +
                                    "DELETE r",
                            Map.of(
                                    "userId", request.getUserId(),
                                    "postId", request.getPostId()
                            )
                    );
                    return null;
                });
                LOG.infof("User %s unliked post %s", request.getUserId(), request.getPostId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process like/unlike", e);
        }
    }

    /**
     * Gets the users who liked a specific post.
     * @param postId the post for which to get the users who liked it
     * @return a list of userIds who liked the specified post
     */
    public List<String> getLikeUsers(String postId) {
        try (var session = neo4jDriver.session()) {
            String cypher = "MATCH (u:User)-[r:LIKES]->(p:Post {postId: $postId}) " +
                            "RETURN u.userId";
            var result = session.executeRead(tx ->
                    tx.run(cypher, Map.of("postId", postId)).list()
            );
            List<String> users = new ArrayList<>();
            for (Record record : result) {
                users.add(record.get("u.userId").asString());
            }
            return users;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve users who liked post", e);
        }
    }

    /**
     * Gets the posts liked by a specific user.
     * @param userId the user for whom to get the posts they liked
     * @return a list of postIds that the specified userId liked
     */
    public List<String> getLikesPosts(String userId) {
        try (var session = neo4jDriver.session()) {
            String cypher = "MATCH (u:User {userId: $userId})-[r:LIKES]->(p:Post) " +
                            "RETURN p.postId";
            var result = session.executeRead(tx ->
                    tx.run(cypher, Map.of("userId", userId)).list()
            );
            List<String> likedPosts = new ArrayList<>();
            for (Record record : result) {
                likedPosts.add(record.get("p.postId").asString());
            }
            return likedPosts;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve liked posts", e);
        }
    }

    /**
     * Checks if a user exists in the database.
     * @param userId the userId to check
     * @return a boolean indicating whether the user exists
     */
    public boolean userExists(String userId) {
        try (var session = neo4jDriver.session()) {
            String cypher = "MATCH (u:User {userId: $userId}) RETURN count(u) as count";
            var result = session.executeRead(tx ->
                    tx.run(cypher, Map.of("userId", userId)).single()
            );
            return result.get("count").asLong() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to check user existence", e);
        }
    }

    /**
     * Checks if a post exists in the database.
     * @param postId the postId to check
     * @return a boolean indicating whether the post exists
     */
    public boolean postExists(String postId) {
        try (var session = neo4jDriver.session()) {
            String cypher = "MATCH (p:Post {postId: $postId}) RETURN count(p) as count";
            var result = session.executeRead(tx ->
                    tx.run(cypher, Map.of("postId", postId)).single()
            );
            return result.get("count").asLong() > 0;
        } catch (Exception e) {
            LOG.errorf("Failed to check if post %s exists: %s", postId, e.getMessage());
            throw new RuntimeException("Failed to check post existence", e);
        }
    }

    /**
     * Enum to know with type of Resource to created
     */
    public static enum TypeCreate{
        USER("User"),
        POST("Post"),
        ;

        private final String typeStr;

        TypeCreate(final String typeStr) {
            this.typeStr = typeStr;
        }

        @Override
        public String toString() {
            return typeStr;
        }
    }

    /**
     * Create a resource (testing purpose).
     *
     * @param resourcesId the list of ID of the all resource need to be created
     * @param typeCreate the type of resource to create
     */
    public void createResource(List<String> resourcesId, TypeCreate typeCreate)
    {
        try (var session = neo4jDriver.session())
        {
            Optional<String> createCypher = resourcesId.stream()
                .map(resource -> String.format(
                    "MERGE (%s:%s {%sId: \"%s\"}) ",
                    resource,
                    typeCreate.toString(),
                    typeCreate.toString().toLowerCase(),
                    resource)
                )
                .reduce((a, b) -> a + b);

            if (createCypher.isPresent())
            {
                session.executeWrite(tx ->
                {
                    tx.run(
                        createCypher.get()
                    );
                    return null;
                });
                resourcesId.forEach(user ->
                    LOG.infof("%s: %s created", typeCreate.toString(), user)
                );
            }
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to process createUser", e);
        }
    }

    /**
     * Clean/Drop Table (testing purpose).
     */
    public void clean()
    {
        try (var session = neo4jDriver.session())
        {
            session.executeWrite(tx ->
            {
                tx.run("MATCH (n) DETACH DELETE n"
                );
                return null;
            });
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to process createUser", e);
        }
        LOG.infof("All table cleaned");
    }
}
