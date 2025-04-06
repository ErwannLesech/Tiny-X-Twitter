package com.epita.service;

import com.epita.payloads.homeTimeline.PostHomeTimeline;
import com.epita.payloads.social.BlockUser;
import com.epita.payloads.social.FollowUser;
import com.epita.payloads.social.LikePost;
import com.epita.repository.HomeTimelineRepository;
import com.epita.repository.entity.HomeTimelineEntry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class HomeTimelineService {

    @Inject
    HomeTimelineRepository homeRepository;

    public Response getTimeline(final ObjectId userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<HomeTimelineEntry> timeline = homeRepository.getTimeline(userId);

        return ( timeline != null ) ? Response.ok(timeline).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    public void updateOnLike(LikePost message) {

    }

    public void updateOnPost(PostHomeTimeline message) {}

    public void updateOnBlock(BlockUser message) {}

    public void updateOnFollow(FollowUser message) {}
}
