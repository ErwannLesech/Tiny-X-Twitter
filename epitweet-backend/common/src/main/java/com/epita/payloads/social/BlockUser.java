package com.epita.payloads.social;

import org.bson.types.ObjectId;

public record BlockUser(ObjectId userId, ObjectId userBlockedId, String method) {
}
