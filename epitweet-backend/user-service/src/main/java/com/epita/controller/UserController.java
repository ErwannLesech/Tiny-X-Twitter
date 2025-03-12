package com.epita.controller;

import com.epita.controller.contracts.UserRequest;
import com.epita.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;

@Slf4j
@ApplicationScoped
@Path("/api/users")
public class UserController {

    @Inject
    UserService userService;

    @Inject
    Logger logger;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(UserRequest userRequest)
    {
        if (!isRequestValid(userRequest, false)) {
            logger.warnf("Invalid request %s", userRequest.toString());
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }
        Boolean creationDone = userService.createUser(userRequest);

        if (creationDone) {
            return Response.status(Response.Status.CREATED).build(); // 201
        }

        return Response.status(Response.Status.CONFLICT).build(); // 409 caused by tag not unique
    }

    @PATCH
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(UserRequest userRequest)
    {
        if (!isRequestValid(userRequest, false))
        {
            logger.warnf("Invalid request %s", userRequest.toString());
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        Boolean updateDone = userService.updateUser(userRequest);

        if (updateDone) {
            return Response.status(Response.Status.OK).build(); // 200
        }
        return Response.status(Response.Status.NOT_FOUND).build(); // 404 caused by user not found
    }

    @DELETE
    @Path("/delete/{userTag}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("userTag") String userTag) {
        if (userTag == null || userTag.isEmpty())
        {
            logger.warnf("Invalid request %s", userTag);
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        Boolean deletionDone = userService.deleteUser(userTag);

        if (deletionDone) {
            return Response.status(Response.Status.OK).build(); // 200
        }
        return Response.status(Response.Status.NOT_FOUND).build(); // 404 if user not found
    }

    // I don't know if the second param will be used, but I expected it
    private Boolean isRequestValid(final UserRequest userRequest, final Boolean isBlockedListNeeded) {
        if (userRequest == null) {
            return Boolean.FALSE;
        }

        String tag = userRequest.getTag();
        String pseudo = userRequest.getPseudo();
        List<ObjectId> blockedList = userRequest.getBlockedUsers();

        if (tag == null || tag.isEmpty() || pseudo == null || pseudo.isEmpty()) {
            return Boolean.FALSE;
        }

        if (isBlockedListNeeded && (blockedList == null || blockedList.isEmpty())) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
