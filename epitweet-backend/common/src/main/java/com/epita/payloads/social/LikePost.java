package com.epita.payloads.social;

import co.elastic.clients.util.DateTime;
import org.bson.types.ObjectId;

public record LikePost(ObjectId userId, ObjectId postId, DateTime postLikeDate, String method) {}