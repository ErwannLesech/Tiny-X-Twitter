package com.epita.controller;

import com.epita.controller.contracts.UserRequest;
import com.epita.controller.contracts.UserResponse;
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

    @GET
    @Path("/getUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@HeaderParam("userTag") String userTag) {
        if (userTag == null || userTag.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        UserResponse userResponse = userService.getUser(userTag);

        if (userResponse == null) {
            return Response.status(Response.Status.NOT_FOUND).build(); // 404
        }

        return Response.ok(userResponse).build();
    }

    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authUser(UserRequest userRequest) {
        if (!isRequestValid(userRequest, true))
        {
            logger.warnf("Invalid request: %s", userRequest.toString());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Integer authResult = userService.authUser(userRequest);

        return switch (authResult) {
            case 404 -> Response.status(Response.Status.NOT_FOUND).build();
            case 401 -> Response.status(Response.Status.UNAUTHORIZED).build();
            case 200 -> Response.ok().build();
            default -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        };
    }

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
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(@HeaderParam("userTag") String userTag) {
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

    private Boolean isRequestValid(final UserRequest userRequest, final Boolean isPasswordNeeded) {
        if (userRequest == null) {
            return Boolean.FALSE;
        }

        String tag = userRequest.getTag();
        String pseudo = userRequest.getPseudo();
        String password = userRequest.getPassword();
        List<ObjectId> blockedList = userRequest.getBlockedUsers();

        if (tag == null || tag.isEmpty() || pseudo == null || pseudo.isEmpty()) {
            return Boolean.FALSE;
        }

        if (isPasswordNeeded && (password == null || password.isEmpty())) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
