package com.epita.payloads.social;

import co.elastic.clients.util.DateTime;
import org.bson.types.ObjectId;

public record FollowUser(ObjectId userId, ObjectId userFollowedId, String method) {}
