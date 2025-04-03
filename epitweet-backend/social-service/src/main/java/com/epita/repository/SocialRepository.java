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

@ApplicationScoped
public class SocialRepository {
    private static final Logger LOG = Logger.getLogger(SocialRepository.class);
    @Inject
    Driver neo4jDriver;

    public void followUnfollow(FollowUnfollowRequest request) {
        try (var session = neo4jDriver.session()) {
            if (request.isFollowUnfollow()) {
                session.executeWrite(tx -> {
                    tx.run(
                            // "MERGE (u1:User {userId: $userFollowId}) " +
                            // "MERGE (u2:User {userId: $userFollowedId}) " +
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
}
