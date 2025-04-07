package com.epita.payloads.homeTimeline;

import org.bson.types.ObjectId;

public record FollowUser(ObjectId userId, ObjectId userFollowedId, String method) {}
