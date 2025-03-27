package com.epita.converter;

import com.epita.payloads.post.CreatePostResponse;
import com.epita.payloads.post.CreatePostRequest;

/**
 * Utility class for converting CreatePostRequest objects to CreatePostResponse objects.
 */
public class CreatePostConverter {

    /**
     * Converts a CreatePostRequest to a CreatePostResponse
     *
     * @param request the CreatePostRequest object to convert
     * @return a CreatePostResponse containing the converted data
     */
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
