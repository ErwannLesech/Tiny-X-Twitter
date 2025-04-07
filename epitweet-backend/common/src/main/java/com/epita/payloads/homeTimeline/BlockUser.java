package com.epita.payloads.homeTimeline;

import org.bson.types.ObjectId;

public record BlockUser(ObjectId userId, ObjectId userBlockedId, String method) {
}
