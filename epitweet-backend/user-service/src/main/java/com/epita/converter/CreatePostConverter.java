package com.epita.converter;

import com.epita.payloads.post.CreatePostResponse;
import com.epita.payloads.post.CreatePostRequest;

/**
 * Utility class for converting CreatePostRequest objects to CreatePostResponse objects.
 */
public class CreatePostConverter {

    public static CreatePostResponse toCreatePostResponse(CreatePostRequest request,
                                                          Boolean parentUserBlockedUser,
                                                          Boolean parentUserBlockedParentUser) {
        return new CreatePostResponse(
                request.getUserId(),
                request.getPostType(),
                request.getContent(),
                request.getMediaUrl(),
                request.getParentId(),
                parentUserBlockedUser,
                parentUserBlockedParentUser
        );
    }
}
