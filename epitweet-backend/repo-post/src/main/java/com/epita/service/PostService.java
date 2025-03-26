package com.epita.service;

import com.epita.controller.contracts.PostRequest;
import com.epita.controller.contracts.PostResponse;
import com.epita.payloads.post.CreatePostResponse;
import com.epita.converter.PostConverter;
import com.epita.repository.publisher.CreatePostPublisher;
import com.epita.repository.PostRepository;
import com.epita.repository.entity.Post;
import com.epita.repository.entity.PostType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class PostService {

    @Inject
    PostRepository postRepository;

    @Inject
    CreatePostPublisher createPostPublisher;

    public List<PostResponse> getPosts(ObjectId userId) {
        List<Post> posts = postRepository.findByUser(userId);

        List<PostResponse> postResponses = new ArrayList<>();
        for (Post post : posts) {
            postResponses.add(PostConverter.toResponse(post));
        }

        return postResponses;
    }

    public PostResponse getPost(ObjectId postId) {
        Post post = postRepository.findById(postId);

        if (post == null)
            return null;

        return PostConverter.toResponse(post);
    }

    public PostResponse getReplyPost(ObjectId replyPostId) {
        Post post = postRepository.findById(replyPostId);

        if (post == null || post.postType != PostType.REPLY)
            return null;

        // check if parent post still exists
        Post repliedPost = postRepository.findById(post.parentId);
        if (repliedPost == null)
            post.parentId = null;

        return PostConverter.toResponse(post);
    }

    public PostResponse createPostRequest(ObjectId userId, PostRequest postRequest) {
        if (Objects.equals(postRequest.postType, PostType.POST.toString())) // Independent post, no need block check
        {
            Post createdPost = createPost(userId, postRequest);
            return PostConverter.toResponse(createdPost);
        }
        else
        {
            createPostPublisher.publish(PostConverter.toCreatePostRequest(userId, postRequest));
            return null;
        }
    }

    public void createPostResponse(CreatePostResponse createPostResponse) {
        if (createPostResponse.parentUserBlockedUser || createPostResponse.userBlockedParentUser)
            return;

        ObjectId userId = createPostResponse.userId;
        PostRequest postRequest = PostConverter.toRequest(createPostResponse);

        createPost(userId, postRequest);
    }

    public Post createPost(ObjectId userId, PostRequest postRequest) {
        Post post = PostConverter.toEntity(userId, postRequest);

        postRepository.createPost(post);

        return post;
    }

    public PostResponse deletePost(ObjectId postId) {
        Post post = postRepository.findById(postId);

        if (post == null)
            return null;

        PostResponse postResponse = PostConverter.toResponse(post);

        postRepository.deletePost(post);

        return postResponse;
    }
}
