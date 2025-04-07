package com.epita.payloads.homeTimeline;

import org.bson.types.ObjectId;

import java.time.Instant;


public record LikePost(ObjectId userId, ObjectId postId, Instant postLikeDate, String method) {}