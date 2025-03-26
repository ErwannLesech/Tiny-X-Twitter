package com.epita.converter;

import com.epita.controller.subscriber.contracts.CreatePostRequest;
import com.epita.repository.publisher.contracts.CreatePostResponse;

public class CreatePostConverter {

    public static CreatePostResponse toCreatePostResponse(CreatePostRequest request, Boolean parentUserBlockedUser, Boolean parentUserBlockedParentUser) {
        return new CreatePostResponse(
                request.userId,
                request.postType,
                request.content,
                request.mediaUrl,
                request.parentId,
                parentUserBlockedUser,
                parentUserBlockedParentUser
        );
    }
}
