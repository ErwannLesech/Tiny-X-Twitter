package com.epita.controller;

import com.epita.controller.contracts.UserRequest;
import com.epita.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
@Path("/api/users")
public class UserController {

    @Inject
    UserService userService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(UserRequest userRequest)
    {
        if (!isRequestValid(userRequest, false))
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400

        Boolean creationDone = userService.createUser(userRequest);

        if (creationDone) {
            return Response.status(Response.Status.CREATED).build(); // 201
        }

        return Response.status(Response.Status.CONFLICT).build(); // 409 caused by tag not unique
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(UserRequest userRequest)
    {
        if (!isRequestValid(userRequest, false))
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400

        Boolean updateDone = userService.updateUser(userRequest);

        if (updateDone) {
            return Response.status(Response.Status.OK).build(); // 200
        }
        return Response.status(Response.Status.NOT_FOUND).build(); // 404 caused by user not found
    }

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(UserRequest userRequest) {
        if (!isRequestValid(userRequest, false)) {
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        Boolean deletionDone = userService.deleteUser(userRequest);

        if (deletionDone) {
            return Response.status(Response.Status.OK).build(); // 200
        }
        return Response.status(Response.Status.NOT_FOUND).build(); // 404 if user not found
    }


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
