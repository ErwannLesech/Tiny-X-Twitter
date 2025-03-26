package com.epita.converter;

import com.epita.payloads.post.CreatePostResponse;
import com.epita.payloads.post.CreatePostRequest;

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
