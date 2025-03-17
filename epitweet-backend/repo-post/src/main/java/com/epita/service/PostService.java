package com.epita.service;

import com.epita.controller.contracts.PostRequest;
import com.epita.controller.contracts.PostResponse;
import com.epita.controller.subscriber.contracts.CreatePostResponse;
import com.epita.repository.publisher.CreatePostPublisher;
import com.epita.repository.PostRepository;
import com.epita.repository.publisher.contracts.CreatePostRequest;
import com.epita.repository.entity.Post;
import com.epita.repository.entity.PostType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.time.Instant;
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
            postResponses.add(new PostResponse(post));
        }

        return postResponses;
    }

    public PostResponse getPost(ObjectId postId) {
        Post post = postRepository.findById(postId);

        if (post == null)
            return null;

        return new PostResponse(post);
    }

    public PostResponse getReplyPost(ObjectId replyPostId) {
        Post post = postRepository.findById(replyPostId);

        if (post == null || post.postType != PostType.REPLY)
            return null;

        Post repliedPost = postRepository.findById(post.parentId);

        if (repliedPost == null)
            return null;

        return new PostResponse(post);
    }

    public void createPostRequest(ObjectId userId, PostRequest postRequest) {
        if (Objects.equals(postRequest.postType, PostType.POST.toString())) // Independent post, no need block check
        {
            createPost(userId, postRequest);
        }
        else
        {
            createPostPublisher.publish(new CreatePostRequest(userId, postRequest));
        }
    }

    public void createPostResponse(CreatePostResponse createPostResponse) {
        if (createPostResponse.parentUserBlockedUser || createPostResponse.userBlockedParentUser)
            return;

        ObjectId userId = createPostResponse.userId;
        PostRequest postRequest = new PostRequest(createPostResponse);

        createPost(userId, postRequest);
    }

    public void createPost(ObjectId userId, PostRequest postRequest) {
        Post post = new Post(userId, postRequest);

        postRepository.createPost(post);
    }

    public PostResponse deletePost(ObjectId postId) {
        Post post = postRepository.findById(postId);

        if (post == null)
            return null;

        PostResponse postResponse = new PostResponse(post);

        postRepository.deletePost(post);

        return postResponse;
    }
}
