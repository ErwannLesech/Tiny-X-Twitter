package com.epita.controller.contracts;

import com.epita.contracts.post.PostResponse;

/**
 * Response to get home user timeline
 */
public record HomeTimelineResponse(PostResponse post) {}
